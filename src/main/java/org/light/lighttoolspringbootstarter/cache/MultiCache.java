package org.light.lighttoolspringbootstarter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.light.lighttoolspringbootstarter.properties.MultiCacheProperties;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 缓存工具类
 * @author Gaoziyang
 * @since 2022-10-07 17:30:00
 */
@Component
@SuppressWarnings("all")
public class MultiCache implements InitializingBean {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MultiCacheProperties multiCacheProperties;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 同步手动加载缓存
     */
    private Cache<String, Object> caffeineCache;
    /**
     * Redis 的过期时间
     */
    private long redisExpireDuration;
    /**
     * Redis过期时间单位
     */
    private TimeUnit redisExpireUnit;
    /**
     * 布隆过滤器
     */
    private RBloomFilter<String> bloomFilter;

    private static final HashedWheelTimer WHEEL_TIMER = new HashedWheelTimer();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化 Caffeine 本地缓存
        redisExpireDuration = multiCacheProperties.getRedisExpireDuration();
        redisExpireUnit = multiCacheProperties.getRedisExpireUnit();
        caffeineCache = Caffeine.newBuilder()
                .initialCapacity(multiCacheProperties.getInitialCapacity())
                .maximumSize(multiCacheProperties.getMaximumSize() == -1 ? Integer.MAX_VALUE : multiCacheProperties.getMaximumSize())
                .expireAfterAccess(multiCacheProperties.getExpireDuration(), multiCacheProperties.getExpireUnit())
                .recordStats()
                .build();

        // 初始化布隆过滤器
        bloomFilter = redissonClient.getBloomFilter("MultiCacheBloomFilter");
        bloomFilter.tryInit(5000, 0.05);
    }

    /**
     * 同步手动获取数据
     * @param key 键
     * @return 值
     * @param <R> 数据类型
     */
    public <R> R get(String key) {
        return (R) caffeineCache.get(key, k -> {
            // 通过布隆过滤器判断是否存在Key，用于解决缓存穿透
            if (multiCacheProperties.isEnableBloomFilter()) {
                if (!bloomFilter.contains(k)) return null;
            }
            return (R) redisTemplate.opsForValue().get(k);
        });
    }

    /**
     * 同步手动获取数据，如果本地缓存和 Redis 缓存都获取不到，则提供自定义方法
     * @param key 键
     * @return 值
     * @param <R> 数据类型
     */
    public <R> R get(String key, Supplier<R> supplier) {
        return Optional.ofNullable((R) caffeineCache.get(key, k -> {
            if (multiCacheProperties.isEnableBloomFilter()) {
                boolean contains = bloomFilter.contains(key);
                if (!contains) return null;
            }
            R value = (R) redisTemplate.opsForValue().get(k);
            return value;
            }))
        .orElseGet(() -> {
            if (supplier == null) return null;
            // 操作数据库时加分布式，锁防止高并发情况时大量请求到数据库层
            RLock lock = redissonClient.getLock("MultiCacheLock:" + key);
            lock.lock(30, TimeUnit.SECONDS);
            R v;
            try {
                v = supplier.get();
            } finally {
                lock.unlock();
            }
            put(key, v);
            return v;
        });
    }

    /**
     * 批量获取数据
     * @param keys 键集合
     * @return 数据集合
     * @param <R> 数据类型
     */
    public <R> Map<String, R> getAll(List<String> keys) {
        Map<String, Object> values = caffeineCache.getAllPresent(keys);
        if (values != null && !values.isEmpty()) {
            return values.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> (R) entry.getValue()));
        }
        List<Object> objects = redisTemplate.opsForValue().multiGet(keys);
        // 重置 Redis 缓存的存活时间
        if (objects != null && !objects.isEmpty()) {
            for (int i = 0; i < objects.size(); i++) {
                values.put(keys.get(i), objects.get(i));
            }
            caffeineCache.putAll(values);
            return values.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> (R) entry.getValue()));
        }
        return null;
    }

    /**
     * 添加缓存
     * @param key 键
     * @param value 值
     * @return 是否缓存成功
     */
    public boolean put(String key, Object value) {
        return put(key, value, null,  redisExpireDuration, redisExpireUnit);
    }

    /**
     * 添加缓存
     * @param key 键
     * @param value 值
     * @param insertOperation 数据库新增操作
     * @return 是否缓存成功
     */
    public boolean put(String key, Supplier<?> insertOperation) {
        return put(key, null, insertOperation, redisExpireDuration, redisExpireUnit);
    }

    /**
     * 添加缓存，并设置 Redis 超时时间
     * @param key 键
     * @param value 值
     * @param expireDuration Redis 过期时间
     * @param expireUnit Redis 过期时间单位
     * @return 是否缓存成功
     */
    public boolean put(String key, Object value, long expireDuration, TimeUnit unit) {
        return put(key, value, null, expireDuration, unit);
    }


    /**
     * 添加缓存，并设置 Redis 超时时间
     * @param key 键
     * @param value 值
     * @param insertOperation 数据库新增操作
     * @param expireDuration Redis 过期时间
     * @param expireUnit Redis 过期时间单位
     * @return 是否缓存成功
     */
    public boolean put(String key, Supplier<?> insertOperation, long expireDuration, TimeUnit unit) {
        return put(key, null, insertOperation, expireDuration, unit);
    }

    /**
     * 添加缓存
     * @param key 键
     * @param value 值
     * @param insertOperation 数据库新增操作
     * @param expireDuration Redis 过期时间
     * @param expireUnit Redis 过期时间单位
     * @return 是否缓存成功
     */
    private boolean put(String key, Object defaultValue, Supplier<?> insertOperation, long expireDuration, TimeUnit unit) {
        Object v = insertOperation != null ? insertOperation.get() : defaultValue;
        if (insertOperation != null && v == null) return false;
        bloomFilter.add(key);
        redisTemplate.opsForValue().set(key, defaultValue, expireDuration, unit);
        caffeineCache.put(key, defaultValue);
        return true;
    }

    /**
     * 不存在时添加缓存
     * @param key 键
     * @param value 值
     */
    public void putIfAbsent(String key, Object value) {
        redisTemplate.opsForValue().setIfAbsent(key, value, redisExpireDuration, redisExpireUnit);
        if (caffeineCache.getIfPresent(key) == null) {
            caffeineCache.put(key, value);
        }
    }

    /**
     * 不存在时添加缓存，并设置 Redis 超时时间
     * @param key 键
     * @param value 值
     * @param expireDuration 过期时间
     * @param unit 时间单位
     */
    public void putIfAbsent(String key, Object value, long expireDuration, TimeUnit unit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, expireDuration, unit);
        if (caffeineCache.getIfPresent(key) == null) {
            caffeineCache.put(key, value);
        }
    }

    /**
     * 批量添加缓存
     * @param values 缓存集合
     */
    public void putAll(Map<String, Object> values) {
        values.forEach((key, value) -> bloomFilter.add(key));
        redisTemplate.opsForValue().multiSet(values);
        values.forEach((key, value) -> redisTemplate.expire(key, redisExpireDuration, redisExpireUnit));
        caffeineCache.putAll(values);
    }

    /**
     * 批量添加缓存
     * @param values 缓存集合
     * @param expireDuration Redis 过期时间
     * @param expireUnit Redis 过期时间单位
     */
    public void putAll(Map<String, Object> values, int expireDuration, TimeUnit unit) {
        redisTemplate.opsForValue().multiSet(values);
        values.forEach((key, value) -> redisTemplate.expire(key, expireDuration, unit));
        caffeineCache.putAll(values);
    }

    /**
     * 更新数据库和缓存数据
     * @param key 键
     * @param updateOperation 更新操作，返回是否更新成功
     */
    public void update(String key, Supplier<Boolean> updateOperation) {
        if (updateOperation != null ? updateOperation.get() : true) {
            delete(key);
        }
    }

    /**
     * 延迟双删
     * @param key 键
     */
    public void delete(String key) {
        // 第一次删除
        doDelete(key);

        // 延迟第二次删除
        WHEEL_TIMER.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                doDelete(key);
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * 删除缓存
     * @param key 键
     * @param deleteOperation 删除操作
     * @return 是否删除成功
     */
    public boolean delete(String key, Supplier<Boolean> deleteOperation) {
        update(key, deleteOperation);
        return false;
    }

    /**
     * 批量删除缓存
     * @param key 键
     */
    public void deleteAll(List<String> keys) {
        keys.forEach(this::delete);
    }

    /**
     * 清理某个本地缓存
     * @param key 要清理的缓存
     */
    public void deleteLocal(String key) {
        caffeineCache.invalidate(key);
    }

    /**
     * 清空所有本地缓存
     */
    public void deleteLocalAll() {
        caffeineCache.invalidateAll();
    }

    /**
     * 清理某些本地缓存
     * @param keys 要清理的缓存
     */
    public void deleteLocalAll(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            deleteLocalAll();
        }
        caffeineCache.invalidateAll(keys);
    }

    /**
     * 删除缓存
     * @param key 键
     * @return 是否删除成功
     */
    private boolean doDelete(String key) {
        boolean delete = redisTemplate.delete(key);
        if (delete) {
            redisTemplate.convertAndSend("CleanKeyBroadcaster", key);
            return true;
        }
        return false;
    }

    /**
     * 获取缓存统计数据
     * @return 缓存统计数据
     */
    public String stats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== Caffeine缓存详情 ==========\n");
        sb.append("总请求数：" + caffeineCache.stats().requestCount() + "\n");
        sb.append("缓存命中数：" + caffeineCache.stats().hitCount() + "\n");
        sb.append("缓存命中比例：" + BigDecimal
                .valueOf(caffeineCache.stats().hitRate())
                .multiply(BigDecimal.valueOf(100L))
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue() + "%\n");
        sb.append("缓存未命中数：" + caffeineCache.stats().missCount() + "\n");
        sb.append("缓存未命中比例：" + BigDecimal
                .valueOf(caffeineCache.stats().missRate())
                .multiply(BigDecimal.valueOf(100L))
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue() + "%\n\n");
        sb.append("缓存加载成功的次数：" + caffeineCache.stats().loadSuccessCount() + "\n");
        sb.append("缓存加载失败的次数：" + caffeineCache.stats().loadFailureCount() + "\n");
        sb.append("缓存加载失败的比例：" + BigDecimal
                .valueOf(caffeineCache.stats().loadFailureRate())
                .multiply(BigDecimal.valueOf(100L))
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue() + "%\n\n");
        sb.append("缓存淘汰总数：" + caffeineCache.stats().evictionCount() + "\n");
        sb.append("缓存淘汰总权重：" + caffeineCache.stats().evictionWeight() + "\n\n");
        sb.append("总加载时间：" + caffeineCache.stats().totalLoadTime() * 1000 + " ms\n");
        sb.append("平均加载时间：" + caffeineCache.stats().averageLoadPenalty() * 1000 + " ms\n");
        sb.append("=======================================\n");
        return sb.toString();
    }
}

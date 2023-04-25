package org.light.tool.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 多级缓存的属性配置
 * @author Gaoziyang
 * @since 2022-10-15 09:52:27
 */
@Data
@Primary
@RefreshScope
@Configuration
@EnableConfigurationProperties(MultiCacheProperties.class)
@ConfigurationProperties(prefix = "light.multi-cache")
public class MultiCacheProperties {
    /**
     * 默认初始容量
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 128;

    /**
     * 默认最大容量
     */
    private static final int DEFAULT_MAXIMUM_SIZE = 10_000;

    /**
     * 默认过期时间
     */
    private static final long DEFAULT_EXPIRE_DURATION = 10L;

    /**
     * 默认本地缓存过期时间单位
     */
    private static final TimeUnit DEFAULT_EXPIRE_UNIT = TimeUnit.SECONDS;

    /**
     * 默认Redis过期时间单位
     */
    private static final TimeUnit DEFAULT_REDIS_EXPIRE_UNIT = TimeUnit.SECONDS;

    /**
     * 初始容量
     */
    private int initialCapacity = DEFAULT_INITIAL_CAPACITY;

    /**
     * 最大容量，-1表示无限制
     */
    private int maximumSize = DEFAULT_MAXIMUM_SIZE;

    /**
     * 过期时间
     */
    private long expireDuration = DEFAULT_EXPIRE_DURATION;

    /**
     * Redis 过期时间
     */
    private long redisExpireDuration = DEFAULT_EXPIRE_DURATION * 3L;

    /**
     * 过期时间单位
     */
    private TimeUnit expireUnit = DEFAULT_EXPIRE_UNIT;

    /**
     * Redis过期时间单位
     */
    private TimeUnit redisExpireUnit = DEFAULT_REDIS_EXPIRE_UNIT;

    /**
     * 是否开启布隆过滤器，用于解决缓存穿透问题
     */
    private boolean enableBloomFilter = false;
}

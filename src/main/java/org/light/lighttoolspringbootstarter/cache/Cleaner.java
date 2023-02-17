package org.light.lighttoolspringbootstarter.cache;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 本地缓存清理器
 * @author Gaoziyang
 * @since 2023-02-11 17:19:48
 */
@Component
public class Cleaner {
    @Resource
    private MultiCache multiCache;
    @Resource
    private FastJsonRedisSerializer<String> fastJsonRedisSerializer;

    /**
     * 清理本地缓存
     * @param key 要清理的本地缓存
     */
    public void clean(String key) {
        if (StringUtils.hasText(key)) {
            multiCache.deleteLocal(fastJsonRedisSerializer.deserialize(key.getBytes()));
        }
    }
}

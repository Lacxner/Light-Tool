package org.light.tool.core;

import org.light.tool.cache.Cleaner;
import org.light.tool.cache.MultiCache;
import org.light.tool.config.RedisTemplateConfig;
import org.light.tool.properties.MultiCacheProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置类
 * @author Gaoziyang
 * @since 2022-11-21 10:04:15
 */
@AutoConfiguration
@Import({RedisTemplateConfig.class, MultiCache.class, MultiCacheProperties.class, Cleaner.class})
public class ToolAutoConfiguration {
}

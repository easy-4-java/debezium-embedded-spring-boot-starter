package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * 自定义数据库历史记录配置器。
 */
public class CustomSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.Custom custom = properties.getCustom();
        
        if (custom.getHistoryClass() != null) {
            builder.with("schema.history.internal", custom.getHistoryClass());
            
            // 添加自定义配置属性
            if (custom.getProps() != null) {
                custom.getProps().forEach((key, value) -> {
                    if (key.startsWith("schema.history.internal.")) {
                        builder.with(key, value);
                    } else {
                        builder.with("schema.history.internal." + key, value);
                    }
                });
            }
        }
    }
}

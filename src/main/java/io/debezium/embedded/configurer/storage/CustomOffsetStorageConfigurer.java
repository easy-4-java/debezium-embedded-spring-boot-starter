package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * 自定义 Offset 存储配置。
 */
public class CustomOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Custom custom = properties.getCustom();
        
        if (custom.getClassName() != null) {
            builder.with("offset.storage", custom.getClassName());
            
            // 添加自定义配置属性
            if (custom.getProps() != null) {
                custom.getProps().forEach((key, value) -> {
                    if (key.startsWith("offset.storage.")) {
                        builder.with(key, value);
                    } else {
                        builder.with("offset.storage." + key, value);
                    }
                });
            }
        }
    }
}



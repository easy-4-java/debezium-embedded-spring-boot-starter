package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * 文件数据库历史记录配置器。
 */
public class FileSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {

        DebeziumSchemaHistoryProperties.File file = properties.getFile();

        // Internal schema history store
        builder.with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(file::getFilename).whenHasText().to(value -> builder.with("schema.history.internal.file", value));

    }
}

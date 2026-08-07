package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * Memory 数据库历史记录配置器。
 */
public class MemorySchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * Memory 历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        // Internal schema history store
        builder.with("schema.history.internal", "io.debezium.relational.history.MemorySchemaHistory");
    }

}

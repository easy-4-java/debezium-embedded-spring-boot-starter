package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * JDBC 数据库历史记录配置器。
 */
public class JdbcSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.Jdbc jdbc = properties.getJdbc();
        
        builder.with("schema.history.internal", "io.debezium.storage.jdbc.history.JdbcSchemaHistory");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础连接配置
        map.from(jdbc::getUrl).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.connection.url", value));
        map.from(jdbc::getUsername).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.connection.user", value));
        map.from(jdbc::getPassword).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.connection.password", value));
        map.from(jdbc::getTableName).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.table.name", value));
        
        // 连接池配置
        map.from(jdbc::getPoolSize).to(value -> builder.with("schema.history.internal.jdbc.connection.pool.size", value));
        map.from(jdbc::getMinConnections).to(value -> builder.with("schema.history.internal.jdbc.connection.pool.min.connections", value));
        map.from(jdbc::getMaxConnections).to(value -> builder.with("schema.history.internal.jdbc.connection.pool.max.connections", value));
        map.from(jdbc::getMaxConnectionLifetime).to(value -> builder.with("schema.history.internal.jdbc.connection.pool.max.lifetime.ms", value));
        map.from(jdbc::getMaxConnectionIdleTime).to(value -> builder.with("schema.history.internal.jdbc.connection.pool.max.idle.time.ms", value));
        
        // 超时配置
        map.from(jdbc::getConnectionTimeout).to(value -> builder.with("schema.history.internal.jdbc.connection.timeout.ms", value));
        map.from(jdbc::getQueryTimeout).to(value -> builder.with("schema.history.internal.jdbc.query.timeout.ms", value));
        map.from(jdbc::getConnectionValidationTimeout).to(value -> builder.with("schema.history.internal.jdbc.connection.validation.timeout.ms", value));
        
        // 连接验证配置
        map.from(jdbc::getConnectionValidationQuery).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.connection.validation.query", value));
        map.from(jdbc::getLeakDetectionThreshold).to(value -> builder.with("schema.history.internal.jdbc.connection.leak.detection.threshold", value));
        map.from(jdbc::getLeakDetectionThresholdMs).to(value -> builder.with("schema.history.internal.jdbc.connection.leak.detection.threshold.ms", value));
        
        // 事务配置
        map.from(jdbc::getAutoCommit).to(value -> builder.with("schema.history.internal.jdbc.auto.commit", value));
        map.from(jdbc::getTransactionIsolation).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.transaction.isolation", value));
        
        // SSL 配置
        map.from(jdbc::getUseSSL).to(value -> builder.with("schema.history.internal.jdbc.use.ssl", value));
        map.from(jdbc::getSslMode).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.ssl.mode", value));
        map.from(jdbc::getVerifyServerCertificate).to(value -> builder.with("schema.history.internal.jdbc.verify.server.certificate", value));
        map.from(jdbc::getAllowPublicKeyRetrieval).to(value -> builder.with("schema.history.internal.jdbc.allow.public.key.retrieval", value));
        
        // 字符和时区配置
        map.from(jdbc::getCharacterEncoding).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.character.encoding", value));
        map.from(jdbc::getTimezone).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.timezone", value));
        
        // 重试配置
        map.from(jdbc::getMaxRetries).to(value -> builder.with("schema.history.internal.jdbc.connection.retry.max.attempts", value));
        map.from(jdbc::getRetryDelayMs).to(value -> builder.with("schema.history.internal.jdbc.connection.retry.delay.ms", value));
        
        // 表配置
        map.from(jdbc::getTableDdl).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.table.ddl", value));
        map.from(jdbc::getTableSelect).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.table.select", value));
        map.from(jdbc::getTableExistSelect).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.table.exists.select", value));
        map.from(jdbc::getTableInsert).whenHasText().to(value -> builder.with("schema.history.internal.jdbc.table.insert", value));
    }
}

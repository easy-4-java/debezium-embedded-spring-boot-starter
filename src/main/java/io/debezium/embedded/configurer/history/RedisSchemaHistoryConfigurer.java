package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Redis 数据库历史记录配置器。
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class RedisSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.Redis redis = properties.getRedis();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        builder.with("schema.history.internal", "io.debezium.storage.redis.history.RedisSchemaHistory");
        
        // 基础配置
        map.from(redis::getKey).whenHasText().to(value -> builder.with("schema.history.internal.redis.key", value));
        map.from(redis::getAddress).whenHasText().to(value -> builder.with("schema.history.internal.redis.address", value));
        map.from(redis::getUser).whenHasText().to(value -> builder.with("schema.history.internal.redis.user", value));
        map.from(redis::getPassword).whenHasText().to(value -> builder.with("schema.history.internal.redis.password", value));
        map.from(redis::getDbIndex).to(value -> builder.with("schema.history.internal.redis.db.index", value));
        
        // SSL 配置
        map.from(redis::getSslEnabled).to(value -> builder.with("schema.history.internal.storage.redis.ssl.enabled", value));
        map.from(redis::getSslHostnameVerificationEnabled).to(value -> builder.with("schema.history.internal.storage.redis.ssl.hostname.verification.enabled", value));
        map.from(redis::getSslTruststorePath).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.truststore.path", value));
        map.from(redis::getSslTruststorePassword).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.truststore.password", value));
        map.from(redis::getSslTruststoreType).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.truststore.type", value));
        map.from(redis::getSslKeystorePath).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.keystore.path", value));
        map.from(redis::getSslKeystorePassword).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.keystore.password", value));
        map.from(redis::getSslKeystoreType).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.keystore.type", value));
        
        // 超时配置
        map.from(redis::getConnectionTimeoutMs).to(value -> builder.with("schema.history.internal.storage.redis.connection.timeout.ms", value));
        map.from(redis::getSocketTimeoutMs).to(value -> builder.with("schema.history.internal.storage.redis.socket.timeout.ms", value));
        
        // 重试配置
        map.from(redis::getRetryInitialDelayMs).to(value -> builder.with("schema.history.internal.storage.redis.retry.initial.delay.ms", value));
        map.from(redis::getRetryMaxDelayMs).to(value -> builder.with("schema.history.internal.storage.redis.retry.max.delay.ms", value));
        map.from(redis::getRetryMaxAttempts).to(value -> builder.with("schema.history.internal.storage.redis.retry.max.attempts", value));
        
        // 等待配置
        map.from(redis::getWaitEnabled).to(value -> builder.with("schema.history.internal.storage.redis.wait.enabled", value));
        map.from(redis::getWaitTimeoutMs).to(value -> builder.with("schema.history.internal.storage.redis.wait.timeout.ms", value));
        map.from(redis::getWaitRetryEnabled).to(value -> builder.with("schema.history.internal.storage.redis.wait.retry.enabled", value));
        map.from(redis::getWaitRetryDelayMs).to(value -> builder.with("schema.history.internal.storage.redis.wait.retry.delay.ms", value));
    }
}

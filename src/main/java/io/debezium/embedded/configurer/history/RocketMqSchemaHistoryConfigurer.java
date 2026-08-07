package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * RocketMQ 数据库历史记录配置器。
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class RocketMqSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.RocketMq rocketMq = properties.getRocketMq();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        builder.with("schema.history.internal", "io.debezium.storage.rocketmq.history.RocketMqSchemaHistory");
        
        // 严格按照官方文档配置参数
        map.from(rocketMq::getTopic).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.topic", value));
        map.from(rocketMq::getNameSrvAddr).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.name.srv.addr", value));
        map.from(rocketMq::getAclEnabled).to(value -> builder.with("schema.history.internal.rocketmq.acl.enabled", value));
        map.from(rocketMq::getAccessKey).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.access.key", value));
        map.from(rocketMq::getSecretKey).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.secret.key", value));
        map.from(rocketMq::getRecoveryAttempts).to(value -> builder.with("schema.history.internal.rocketmq.recovery.attempts", value));
        map.from(rocketMq::getRecoveryPollIntervalMs).to(value -> builder.with("schema.history.internal.rocketmq.recovery.poll.interval.ms", value));
        map.from(rocketMq::getStoreRecordTimeoutMs).to(value -> builder.with("schema.history.internal.rocketmq.store.record.timeout.ms", value));
    }
}

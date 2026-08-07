package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Kafka 数据库历史记录配置器。
 */
public class KafkaSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.Kafka kafka = properties.getKafka();
        
        builder.with("schema.history.internal", "io.debezium.storage.kafka.history.KafkaSchemaHistory");
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础配置
        map.from(kafka::getTopic).whenHasText().to(value -> builder.with("schema.history.internal.kafka.topic", value));
        map.from(kafka::getBootstrapServers).whenHasText().to(value -> builder.with("schema.history.internal.kafka.bootstrap.servers", value));
        map.from(kafka::getRecoveryAttempts).to(value -> builder.with("schema.history.internal.kafka.recovery.attempts", value));
        map.from(kafka::getRecoveryPollIntervalMs).to(value -> builder.with("schema.history.internal.kafka.recovery.poll.interval.ms", value));
        map.from(kafka::getQueryTimeoutMs).to(value -> builder.with("schema.history.internal.kafka.query.timeout.ms", value));
        map.from(kafka::getCreateTimeoutMs).to(value -> builder.with("schema.history.internal.kafka.create.timeout.ms", value));
        
        // 生产者配置
        DebeziumSchemaHistoryProperties.Kafka.Producer producer = kafka.getProducer();
        map.from(producer::getAcks).whenHasText().to(value -> builder.with("schema.history.internal.kafka.producer.acks", value));
        map.from(producer::getRetries).to(value -> builder.with("schema.history.internal.kafka.producer.retries", value));
        map.from(producer::getBatchSize).to(value -> builder.with("schema.history.internal.kafka.producer.batch.size", value));
        map.from(producer::getLingerMs).to(value -> builder.with("schema.history.internal.kafka.producer.linger.ms", value));
        map.from(producer::getBufferMemory).to(value -> builder.with("schema.history.internal.kafka.producer.buffer.memory", value));
        map.from(producer::getCompressionType).whenHasText().to(value -> builder.with("schema.history.internal.kafka.producer.compression.type", value));
        map.from(producer::getMaxRequestSize).to(value -> builder.with("schema.history.internal.kafka.producer.max.request.size", value));
        map.from(producer::getRequestTimeoutMs).to(value -> builder.with("schema.history.internal.kafka.producer.request.timeout.ms", value));
        map.from(producer::getMetadataMaxAgeMs).to(value -> builder.with("schema.history.internal.kafka.producer.metadata.max.age.ms", value));
        map.from(producer::getConnectionsMaxIdleMs).to(value -> builder.with("schema.history.internal.kafka.producer.connections.max.idle.ms", value));
        map.from(producer::getReconnectBackoffMs).to(value -> builder.with("schema.history.internal.kafka.producer.reconnect.backoff.ms", value));
        map.from(producer::getRetryBackoffMs).to(value -> builder.with("schema.history.internal.kafka.producer.retry.backoff.ms", value));
        
        // 消费者配置
        DebeziumSchemaHistoryProperties.Kafka.Consumer consumer = kafka.getConsumer();
        map.from(consumer::getAutoOffsetReset).whenHasText().to(value -> builder.with("schema.history.internal.kafka.consumer.auto.offset.reset", value));
        map.from(consumer::getEnableAutoCommit).to(value -> builder.with("schema.history.internal.kafka.consumer.enable.auto.commit", value));
        map.from(consumer::getSessionTimeoutMs).to(value -> builder.with("schema.history.internal.kafka.consumer.session.timeout.ms", value));
        map.from(consumer::getHeartbeatIntervalMs).to(value -> builder.with("schema.history.internal.kafka.consumer.heartbeat.interval.ms", value));
        map.from(consumer::getMaxPollRecords).to(value -> builder.with("schema.history.internal.kafka.consumer.max.poll.records", value));
        map.from(consumer::getMaxPollIntervalMs).to(value -> builder.with("schema.history.internal.kafka.consumer.max.poll.interval.ms", value));
        map.from(consumer::getRequestTimeoutMs).to(value -> builder.with("schema.history.internal.kafka.consumer.request.timeout.ms", value));
        map.from(consumer::getFetchMinBytes).to(value -> builder.with("schema.history.internal.kafka.consumer.fetch.min.bytes", value));
        map.from(consumer::getFetchMaxWaitMs).to(value -> builder.with("schema.history.internal.kafka.consumer.fetch.max.wait.ms", value));
        map.from(consumer::getConnectionsMaxIdleMs).to(value -> builder.with("schema.history.internal.kafka.consumer.connections.max.idle.ms", value));
        map.from(consumer::getReconnectBackoffMs).to(value -> builder.with("schema.history.internal.kafka.consumer.reconnect.backoff.ms", value));
        map.from(consumer::getRetryBackoffMs).to(value -> builder.with("schema.history.internal.kafka.consumer.retry.backoff.ms", value));
        
        // 安全配置
        DebeziumSchemaHistoryProperties.Kafka.Security security = kafka.getSecurity();
        map.from(security::getSecurityProtocol).whenHasText().to(value -> builder.with("schema.history.internal.kafka.security.protocol", value));
        map.from(security::getSaslMechanism).whenHasText().to(value -> builder.with("schema.history.internal.kafka.sasl.mechanism", value));
        map.from(security::getSaslUsername).whenHasText().to(value -> builder.with("schema.history.internal.kafka.sasl.username", value));
        map.from(security::getSaslPassword).whenHasText().to(value -> builder.with("schema.history.internal.kafka.sasl.password", value));
        map.from(security::getSslTruststoreLocation).whenHasText().to(value -> builder.with("schema.history.internal.kafka.ssl.truststore.location", value));
        map.from(security::getSslTruststorePassword).whenHasText().to(value -> builder.with("schema.history.internal.kafka.ssl.truststore.password", value));
        map.from(security::getSslKeystoreLocation).whenHasText().to(value -> builder.with("schema.history.internal.kafka.ssl.keystore.location", value));
        map.from(security::getSslKeystorePassword).whenHasText().to(value -> builder.with("schema.history.internal.kafka.ssl.keystore.password", value));
        map.from(security::getSslKeyPassword).whenHasText().to(value -> builder.with("schema.history.internal.kafka.ssl.key.password", value));
        map.from(security::getSslEndpointIdentificationAlgorithm).whenHasText().to(value -> builder.with("schema.history.internal.kafka.ssl.endpoint.identification.algorithm", value));
    }
}

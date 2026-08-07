package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Kafka 型 Offset 存储配置。
 */
public class KafkaOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Kafka kafka = properties.getKafka();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        // Offset Store
        builder.with("offset.storage", "org.apache.kafka.connect.storage.KafkaOffsetBackingStore");
        map.from(kafka::getTopic).whenHasText().to(value -> builder.with("offset.storage.topic", value));
        map.from(kafka::getPartitions).to(value -> builder.with("offset.storage.partitions", value));
        map.from(kafka::getReplicationFactor).to(value -> builder.with("offset.storage.replication.factor", value));
    }
}



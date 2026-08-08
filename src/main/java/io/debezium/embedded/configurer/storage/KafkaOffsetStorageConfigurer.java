package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * {@link OffsetStorageConfigurer} for Kafka topic based offset storage.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class KafkaOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * Applies the storage configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the offset-storage configuration properties
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



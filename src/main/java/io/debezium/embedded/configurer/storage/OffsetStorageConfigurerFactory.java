package io.debezium.embedded.configurer.storage;

import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Factory that resolves the {@link OffsetStorageConfigurer} implementation
 * matching the offset-storage type declared on the supplied properties.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class OffsetStorageConfigurerFactory {

    /**
     * Returns the {@link OffsetStorageConfigurer} for the offset-storage type
     * carried by {@code properties}.
     *
     * @param properties the offset-storage configuration properties
     * @return the matching offset-storage configurer
     * @throws IllegalArgumentException if the offset-storage type is not supported
     */
    public static OffsetStorageConfigurer from(DebeziumOffsetStorageProperties properties) {
        switch (properties.getType()) {
            case MEMORY:
                return new MemoryOffsetStorageConfigurer();
            case FILE:
                return new FileOffsetStorageConfigurer();
            case KAFKA:
                return new KafkaOffsetStorageConfigurer();
            case JDBC:
                return new JdbcOffsetStorageConfigurer();
            case REDIS:
                return new RedisOffsetStorageConfigurer();
            case CUSTOM:
                return new CustomOffsetStorageConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported offset storage type: " + properties.getType());
        }
    }
}



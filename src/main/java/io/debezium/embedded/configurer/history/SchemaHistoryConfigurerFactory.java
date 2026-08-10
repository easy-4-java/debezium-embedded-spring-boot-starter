package io.debezium.embedded.configurer.history;

import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * Factory that resolves the {@link SchemaHistoryConfigurer} implementation
 * matching the schema-history type declared on the supplied properties.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class SchemaHistoryConfigurerFactory {

    /**
     * Returns the {@link SchemaHistoryConfigurer} for the schema-history type
     * carried by {@code historyProperties}.
     *
     * @param historyProperties the schema-history configuration properties
     * @return the matching schema-history configurer
     * @throws IllegalArgumentException if the schema-history type is not supported
     */
    public static SchemaHistoryConfigurer from(DebeziumSchemaHistoryProperties historyProperties) {
        switch (historyProperties.getType()) {
            case FILE:
                return new FileSchemaHistoryConfigurer();
            case KAFKA:
                return new KafkaSchemaHistoryConfigurer();
            case JDBC:
                return new JdbcSchemaHistoryConfigurer();
            case REDIS:
                return new RedisSchemaHistoryConfigurer();
            case S3:
                return new AmazonS3SchemaHistoryConfigurer();
            case ROCKETMQ:
                return new RocketMqSchemaHistoryConfigurer();
            case AZURE_BLOB:
                return new AzureBlobSchemaHistoryConfigurer();
            case CUSTOM:
                return new CustomSchemaHistoryConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported schema history type: " + historyProperties.getType());
        }
    }
}

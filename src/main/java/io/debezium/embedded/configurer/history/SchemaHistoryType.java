package io.debezium.embedded.configurer.history;

/**
 * Enumeration of the schema-history storage backends supported by this starter.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public enum SchemaHistoryType {

    /** File-based schema history. */
    FILE,
    /** Kafka topic based schema history. */
    KAFKA,
    /** JDBC database based schema history. */
    JDBC,
    /** Redis based schema history. */
    REDIS,
    /** Amazon S3 based schema history. */
    S3,
    /** RocketMQ based schema history. */
    ROCKETMQ,
    /** Azure Blob Storage based schema history. */
    AZURE_BLOB,
    /** User-provided custom schema history backend. */
    CUSTOM
}

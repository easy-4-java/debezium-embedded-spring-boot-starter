package io.debezium.embedded.configurer.storage;

/**
 * Enumeration of the offset-storage backends supported by this starter.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public enum OffsetStorageType {

    /** In-memory offset storage (non-persistent, testing only). */
    MEMORY,
    /** File-based offset storage. */
    FILE,
    /** Kafka topic based offset storage. */
    KAFKA,
    /** JDBC database based offset storage. */
    JDBC,
    /** Redis based offset storage. */
    REDIS,
    /** User-provided custom offset-storage backend. */
    CUSTOM
}



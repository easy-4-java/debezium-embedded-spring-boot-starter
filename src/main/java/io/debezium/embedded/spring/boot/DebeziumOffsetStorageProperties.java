package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.storage.OffsetStorageType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for the Debezium connector offset store.
 *
 * <p>The offset store records the position of the connector within the database
 * change stream so that, after a restart, the connector can resume from the
 * correct position instead of reprocessing data or losing events.</p>
 *
 * <p>Supported storage types:</p>
 * <ul>
 *   <li><strong>FILE</strong> — persist offsets to a local file</li>
 *   <li><strong>KAFKA</strong> — persist offsets to a Kafka topic</li>
 *   <li><strong>JDBC</strong> — persist offsets to a relational database</li>
 *   <li><strong>REDIS</strong> — persist offsets to Redis</li>
 *   <li><strong>CUSTOM</strong> — user supplied implementation</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Data
public class DebeziumOffsetStorageProperties {

    /**
     * Offset storage type.
     * <p>Defaults to {@code FILE}. One of {@code FILE}, {@code KAFKA},
     * {@code JDBC}, {@code REDIS}, {@code CUSTOM}.</p>
     */
    private OffsetStorageType type = OffsetStorageType.FILE;

    /** File-based storage configuration (active when {@link #type} is {@code FILE}). */
    private File file = new File();

    /** Kafka-based storage configuration (active when {@link #type} is {@code KAFKA}). */
    private Kafka kafka = new Kafka();

    /** JDBC-based storage configuration (active when {@link #type} is {@code JDBC}). */
    private Jdbc jdbc = new Jdbc();

    /** Redis-based storage configuration (active when {@link #type} is {@code REDIS}). */
    private Redis redis = new Redis();

    /** Custom storage configuration (active when {@link #type} is {@code CUSTOM}). */
    private Custom custom = new Custom();

    /**
     * File based offset storage configuration.
     * <p>The simplest option; suited to single-node deployments that do not require high availability.</p>
     */
    @Data
    public static class File {
        /**
         * Path of the file that stores the connector offsets.
         * <p>Default {@code /tmp/offsets.dat}. An absolute path with read/write
         * permissions for the application is recommended.</p>
         */
        private String fileName = "/tmp/offsets.dat";

        /**
         * Maximum interval (ms) between flushes of the offsets to the file.
         * <p>Default {@code 60000} (60 seconds). Smaller values increase data
         * safety at the cost of higher I/O.</p>
         */
        private Integer flushIntervalMs = 60_000;

        /**
         * Maximum time (ms) to wait for an offset commit to complete.
         * <p>Default {@code 5000}. Throws on timeout. Use a sensible value in
         * production to avoid indefinite blocking.</p>
         */
        private Integer flushTimeoutMs = 5000;

    }

    /**
     * Kafka based offset storage configuration.
     * <p>Suited to distributed deployments and high availability scenarios; multiple
     * connector instances can share offsets through the topic.</p>
     */
    @Data
    public static class Kafka {

        /**
         * Name of the Kafka topic used to store offsets.
         * <p>Default {@code debezium-offsets}. Created automatically if missing.</p>
         */
        private String topic = "debezium-offsets";

        /**
         * Number of partitions for the offset topic. Only used when the topic
         * does not yet exist. Defaults to {@code 25}.
         */
        private Integer partitions = 25;

        /**
         * Replication factor for the offset topic. Only used when the topic
         * does not yet exist. Defaults to {@code 3} (use 3 or higher in production).
         */
        private Integer replicationFactor = 3;



        /** Kafka producer tuning used when writing offsets. */
        private Producer producer = new Producer();

        /** Kafka consumer tuning used when reading offsets. */
        private Consumer consumer = new Consumer();

        /** Security (SSL/SASL) settings for the Kafka connection. */
        private Security security = new Security();

        /** Kafka producer configuration. */
        @Data
        public static class Producer {
            /**
             * Acknowledgement level for produced records.
             * <ul>
             *   <li>{@code 0} — do not wait for any acknowledgement</li>
             *   <li>{@code 1} — wait for the leader acknowledgement</li>
             *   <li>{@code all} — wait for all in-sync replicas</li>
             * </ul>
             * Defaults to {@code all}.
             */
            private String acks = "all";

            /** Number of retries on transient send failures (default {@code 3}). */
            private Integer retries = 3;

            /** Batch size in bytes for records sent together (default {@code 16384} = 16 KB). */
            private Integer batchSize = 16384;

            /** Time (ms) the producer waits to accumulate a larger batch (default {@code 1}). */
            private Integer lingerMs = 1;

            /** Send buffer size in bytes used to hold unsent records (default 32 MB). */
            private Integer bufferMemory = 33554432;

            /**
             * Compression codec for the produced records. One of {@code none},
             * {@code gzip}, {@code snappy}, {@code lz4}, {@code zstd}. Defaults to {@code gzip}.
             */
            private String compressionType = "gzip";

            /** Maximum size (bytes) of a single produce request (default 1 MB). */
            private Integer maxRequestSize = 1048576;

            /** Producer request timeout (ms, default {@code 30000}). */
            private Integer requestTimeoutMs = 30000;

            /** Maximum age (ms) before metadata is refreshed (default {@code 300000}). */
            private Integer metadataMaxAgeMs = 300000;

            /** Maximum idle time (ms) before a connection is closed (default {@code 540000}). */
            private Integer connectionsMaxIdleMs = 540000;

            /** Backoff time (ms) before reconnecting (default {@code 50}). */
            private Integer reconnectBackoffMs = 50;

            /** Backoff time (ms) before retrying a failed request (default {@code 100}). */
            private Integer retryBackoffMs = 100;
        }

        /** Kafka consumer configuration. */
        @Data
        public static class Consumer {
            /**
             * Auto offset reset policy when no committed offset exists.
             * <ul>
             *   <li>{@code earliest} — read from the oldest record</li>
             *   <li>{@code latest} — read only newly produced records</li>
             *   <li>{@code none} — throw an exception</li>
             * </ul>
             * Defaults to {@code earliest}.
             */
            private String autoOffsetReset = "earliest";

            /** Whether offsets are auto-committed (default {@code false}). */
            private Boolean enableAutoCommit = false;

            /** Consumer session timeout (ms, default {@code 30000}); affects group rebalancing. */
            private Integer sessionTimeoutMs = 30000;

            /** Heartbeat interval (ms, default {@code 3000}); must be less than a third of {@code sessionTimeoutMs}. */
            private Integer heartbeatIntervalMs = 3000;

            /** Maximum records returned by a single poll (default {@code 500}). */
            private Integer maxPollRecords = 500;

            /** Maximum delay (ms) between two polls before a rebalance is triggered (default {@code 300000}). */
            private Integer maxPollIntervalMs = 300000;

            /** Consumer request timeout (ms, default {@code 30000}). */
            private Integer requestTimeoutMs = 30000;

            /** Minimum bytes the broker must return (default {@code 1}). */
            private Integer fetchMinBytes = 1;

            /** Maximum time (ms) the broker will wait for enough bytes (default {@code 500}). */
            private Integer fetchMaxWaitMs = 500;

            /** Maximum idle time (ms) before a connection is closed (default {@code 540000}). */
            private Integer connectionsMaxIdleMs = 540000;

            /** Backoff time (ms) before reconnecting (default {@code 50}). */
            private Integer reconnectBackoffMs = 50;

            /** Backoff time (ms) before retrying a failed request (default {@code 100}). */
            private Integer retryBackoffMs = 100;
        }

        /** Kafka security configuration (SSL / SASL). */
        @Data
        public static class Security {
            /**
             * Wire protocol used to talk to Kafka.
             * <ul>
             *   <li>{@code PLAINTEXT} — clear text</li>
             *   <li>{@code SSL} — SSL/TLS encryption</li>
             *   <li>{@code SASL_PLAINTEXT} — SASL auth, clear text</li>
             *   <li>{@code SASL_SSL} — SASL auth, SSL encryption</li>
             * </ul>
             * Defaults to {@code PLAINTEXT}; production should use {@code SSL} or {@code SASL_SSL}.
             */
            private String securityProtocol = "PLAINTEXT";

            /** SASL mechanism (e.g. {@code PLAIN}, {@code SCRAM-SHA-256}, {@code SCRAM-SHA-512}, {@code OAUTHBEARER}). */
            private String saslMechanism;

            /** SASL username; required when SASL is enabled. */
            private String saslUsername;

            /** SASL password; required when SASL is enabled. */
            private String saslPassword;

            /** Path to the SSL truststore file (SSL only). */
            private String sslTruststoreLocation;

            /** Password protecting the SSL truststore (SSL only). */
            private String sslTruststorePassword;

            /** Path to the SSL keystore file (mutual TLS only). */
            private String sslKeystoreLocation;

            /** Password protecting the SSL keystore (mutual TLS only). */
            private String sslKeystorePassword;

            /** Password protecting the SSL private key (mutual TLS only). */
            private String sslKeyPassword;

            /** Endpoint identification algorithm; {@code https} validates the hostname, {@code none} disables it (default {@code https}). */
            private String sslEndpointIdentificationAlgorithm = "https";
        }
    }

    /**
     * JDBC based offset storage configuration.
     * <p>Suited to scenarios that require persistence and transactional guarantees
     * with a relational database backend.</p>
     */
    @Data
    public static class Jdbc {

        /**
         * JDBC connection URL used to reach the database.
         * <p>Example: {@code jdbc:mysql://localhost:3306/debezium}. Required.</p>
         */
        private String offsetStorageUrl;

        /** Database username used for authentication. Required. */
        private String offsetStorageUsername;

        /** Database password used for authentication. Required. */
        private String offsetStoragePassword;

        /**
         * Name of the table that stores the offsets.
         * <p>Default {@code debezium_offset_storage}. Created automatically if missing.</p>
         */
        private String offsetStorageTableName = "debezium_offset_storage";
        /** Optional DDL used to create the offset table. */
        private String offsetStorageTableDdl;
        /** SELECT statement used to read offsets ({@code %s} is replaced with {@link #offsetStorageTableName}). */
        private String offsetStorageTableSelect = "SELECT id, offset_key, offset_val FROM %s ORDER BY record_insert_ts, record_insert_seq";
        /** INSERT statement used to write offsets ({@code %s} is replaced with {@link #offsetStorageTableName}). */
        private String offsetStorageTableInsert = "INSERT INTO %s(id, offset_key, offset_val, record_insert_ts, record_insert_seq) VALUES ( ?, ?, ?, ?, ? )";
        /** DELETE statement used to purge offsets ({@code %s} is replaced with {@link #offsetStorageTableName}). */
        private String offsetStorageTableDelete = "DELETE FROM %s";

        /** Maximum retry attempts on database failures (default {@code 5}). */
        private Integer offsetStorageMaxRetries = 5;

        /** Delay (ms) between retries (default {@code 3000}). */
        private Integer offsetStorageRetryDelayMs = 3000;




    }

    /**
     * Redis based offset storage configuration.
     * <p>Suited to high-throughput, in-memory deployments; supports replication
     * and cluster topologies.</p>
     */
    @Data
    public static class Redis {

        // ==================== Offset Store configuration ====================

        /** Redis key under which offsets are stored (default {@code metadata:debezium:offsets}). */
        private String key = "metadata:debezium:offsets";

        /** Redis server address, format {@code host:port} or {@code redis://host:port}. */
        private String address;

        /** Redis username used for authentication. */
        private String user;

        /** Redis password used for authentication. */
        private String password;

        /** Redis database index (0..15, default {@code 0}). */
        private Integer dbIndex = 0;

        /** Whether SSL/TLS is enabled for the Redis connection (default {@code false}). */
        private Boolean sslEnabled = false;

        /** Whether SSL hostname verification is enabled (default {@code false}). */
        private Boolean sslHostnameVerificationEnabled = false;

        /** Path of the truststore file used for the SSL/TLS connection. */
        private String sslTruststorePath;

        /** Password protecting the truststore file used for the SSL/TLS connection. */
        private String sslTruststorePassword;

        /** Truststore type used for the SSL/TLS connection (default {@code JKS}). */
        private String sslTruststoreType = "JKS";

        /** Path of the keystore file used for the SSL/TLS connection. */
        private String sslKeystorePath;

        /** Password protecting the keystore file used for the SSL/TLS connection. */
        private String sslKeystorePassword;

        /** Keystore type used for the SSL/TLS connection (default {@code JKS}). */
        private String sslKeystoreType = "JKS";

        /** Time (ms) to wait when establishing the Redis connection (default {@code 2000}). */
        private Integer connectionTimeoutMs = 2000;

        /** Socket inactivity timeout (ms); the socket is closed when no data is transferred in time (default {@code 2000}). */
        private Integer socketTimeoutMs = 2000;

        /** Initial delay (ms) before retrying after the first connection failure (default {@code 300}). */
        private Integer retryInitialDelayMs = 300;

        /** Maximum delay (ms) between connection retries (default {@code 10000}). */
        private Integer retryMaxDelayMs = 10000;

        /** Maximum number of connection retries (default {@code 10}). */
        private Integer retryMaxAttempts = 10;

        /** In replicated Redis, whether to wait for replicas to acknowledge writes (default {@code false}). */
        private Boolean waitEnabled = false;

        /** Time (ms) to wait for replica acknowledgement (default {@code 1000}). */
        private Integer waitTimeoutMs = 1000;

        /** Whether to retry failed replica acknowledgement requests (default {@code false}). */
        private Boolean waitRetryEnabled = false;

        /** Delay (ms) between replica acknowledgement retries (default {@code 1000}). */
        private Integer waitRetryDelayMs = 1000;


    }

    /**
     * Custom offset storage configuration.
     * <p>Use this when none of the built-in storage types fit; supply a class
     * implementing {@code org.apache.kafka.connect.storage.OffsetBackingStore}.</p>
     */
    @Data
    public static class Custom {
        /**
         * Fully qualified class name implementing {@code OffsetBackingStore}.
         * <p>Required. Example: {@code com.example.CustomOffsetStore}.</p>
         */
        private String className;

        /** Additional properties passed straight through to the custom implementation. */
        private Map<String, String> props = new HashMap<>();
    }
}



package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.connector.ConnectorType;
import lombok.Data;

/**
 * Configuration properties describing a single Debezium source connector.
 * <p>
 * Common connection fields (host, port, credentials, include/exclude lists) apply
 * across most connectors, while the nested per-database blocks hold connector
 * specific tuning. The active block is selected via {@link #type}.
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Data
public class DebeziumConnectorProperties {

    /**
     * Database connector type. Selects which nested configuration block and
     * connector class are used. Defaults to {@link ConnectorType#MYSQL}.
     */
    private ConnectorType type = ConnectorType.MYSQL;

    /**
     * Logical name of this connector instance; also used as the topic prefix
     * and the destination identifier for offset storage.
     */
    private String destination;

    /** Database host name or IP address. */
    private String host;

    /** Database TCP port. */
    private Integer port;

    /** Database login username. */
    private String username;

    /** Database login password. */
    private String password;

    /** Database name (used by PostgreSQL, Oracle, SQL Server, etc.). */
    private String databaseName;

    /** Replication server id (MySQL only). */
    private String serverId;

    /** Logical server name used to namespace emitted topics. */
    private String serverName;

    /** Comma separated list of databases to include. */
    private String databaseIncludeList;

    /** Comma separated list of databases to exclude. */
    private String databaseExcludeList;

    /** Comma separated list of tables to include (regular-expression format). */
    private String tableIncludeList;

    /** Comma separated list of tables to exclude (regular-expression format). */
    private String tableExcludeList;

    /** Comma separated list of schemas to include (PostgreSQL/SQL Server). */
    private String schemaIncludeList;

    /** Comma separated list of schemas to exclude. */
    private String schemaExcludeList;

    /** MySQL specific connector configuration. */
    private MySql mySql = new MySql();

    /** PostgreSQL specific connector configuration. */
    private PostgreSql postgreSql = new PostgreSql();

    /** MongoDB specific connector configuration. */
    private MongoDb mongoDb = new MongoDb();

    /** Oracle specific connector configuration. */
    private Oracle oracle = new Oracle();

    /** SQL Server specific connector configuration. */
    private SqlServer sqlServer = new SqlServer();

    /** Cassandra specific connector configuration. */
    private Cassandra cassandra = new Cassandra();

    /** Google Spanner specific connector configuration. */
    private Spanner spanner = new Spanner();

    /** Custom connector configuration used when {@link #type} is {@code CUSTOM}. */
    private Custom custom = new Custom();

    /** MySQL connector specific tuning. */
    @Data
    public static class MySql {
        // ==================== Snapshot configuration ====================
        /**
         * Snapshot mode. One of {@code initial}, {@code when_needed}, {@code never},
         * {@code schema_only}, {@code schema_only_recovery}. Defaults to {@code initial}.
         */
        private String snapshotMode = "initial";

        /**
         * Snapshot locking mode. One of {@code minimal}, {@code extended}, {@code none}.
         * Defaults to {@code minimal}.
         */
        private String snapshotLockingMode = "minimal";

        /** Whether to snapshot newly added tables. Defaults to {@code false}. */
        private Boolean snapshotNewTables = false;

        /** Delay (ms) before the snapshot starts. Defaults to {@code 0}. */
        private Long snapshotDelayMs = 0L;

        /** Number of rows fetched per snapshot query. Defaults to {@code 1024}. */
        private Integer snapshotFetchSize = 1024;

        // ==================== Connection / performance configuration ====================
        /** Database connection timeout in milliseconds (default {@code 30000}). */
        private Integer connectTimeoutMs = 30000;

        /** Time (ms) between polls for new change events (default {@code 1000}). */
        private Integer pollIntervalMs = 1000;

        /** Maximum number of change events queued in the blocking queue (default {@code 8192}). */
        private Integer maxQueueSize = 8192;

        /** Maximum number of change events processed in a single batch (default {@code 2048}). */
        private Integer maxBatchSize = 2048;

        /** Minimum row count that triggers result streaming instead of buffering (default {@code 1000}). */
        private Integer minRowCountToStreamResults = 1000;

        /** Maximum queue size in bytes (default 1 GB). */
        private Long maxQueueSizeInBytes = 1073741824L; // 1GB

        // ==================== GTID / replication configuration ====================
        /** Whether GTID sources filter DML events (default {@code true}). */
        private Boolean gtidSourceFilterDmlEvents = true;

        /** Comma separated list of databases included by GTID sources. */
        private String gtidSourceIncludeDatabases;

        /** Comma separated list of databases excluded by GTID sources. */
        private String gtidSourceExcludeDatabases;

        /** Whether GTID sources filter DDL events (default {@code false}). */
        private Boolean gtidSourceFilterDdlEvents = false;

        // ==================== Database connection configuration ====================
        /** Whether the JDBC driver is allowed public key retrieval (default {@code true}). */
        private Boolean allowPublicKeyRetrieval = true;

        /** Whether to use SSL for the JDBC connection (default {@code false}). */
        private Boolean useSSL = false;

        /** Whether the JDBC driver should auto reconnect (default {@code true}). */
        private Boolean autoReconnect = true;

        /** Whether multi-statement queries are permitted (default {@code true}). */
        private Boolean allowMultiQueries = true;

        /**
         * Behaviour for zero date-time values. One of {@code convertToNull},
         * {@code exception}, {@code round}. Defaults to {@code convertToNull}.
         */
        private String zeroDateTimeBehavior = "convertToNull";

        /** JDBC character encoding (default {@code utf8}). */
        private String characterEncoding = "utf8";

        /** Whether the JDBC driver uses Unicode (default {@code true}). */
        private Boolean useUnicode = true;

        /** Server time zone used by the JDBC driver. */
        private String serverTimezone;

        /** Connection time zone used by the JDBC driver. */
        private String connectionTimeZone;

        // ==================== Event processing configuration ====================
        /** Whether to emit a tombstone event after a delete (default {@code false}). */
        private Boolean tombstonesOnDelete = false;

        /** Whether to include the originating SQL query in events (default {@code false}). */
        private Boolean includeQuery = false;

        /** Whether to emit DDL schema-change events (default {@code true}). */
        private Boolean includeSchemaChanges = true;

        /** Whether to enrich events with transaction metadata (default {@code false}). */
        private Boolean provideTransactionMetadata = false;

        // ==================== Performance tuning ====================
        /** Chunk size (rows) used by incremental snapshots (default {@code 1024}). */
        private Integer incrementalSnapshotChunkSize = 1024;

        /** Whether incremental snapshots tolerate concurrent schema changes (default {@code true}). */
        private Boolean incrementalSnapshotAllowSchemaChanges = true;

        /** Fully qualified signal data collection used to drive incremental snapshots. */
        private String signalDataCollection;

        // ==================== Security configuration ====================
        /**
         * SSL mode. One of {@code disabled}, {@code preferred}, {@code required},
         * {@code verify_ca}, {@code verify_identity}. Defaults to {@code disabled}.
         */
        private String sslMode = "disabled";

        /** Path to the SSL truststore. */
        private String sslTruststore;

        /** Password protecting the SSL truststore. */
        private String sslTruststorePassword;

        /** Path to the SSL keystore. */
        private String sslKeystore;

        /** Password protecting the SSL keystore. */
        private String sslKeystorePassword;

        // ==================== Monitoring / debugging ====================
        /** Whether unparseable DDL statements are skipped (default {@code false}). */
        private Boolean databaseHistorySkipUnparseableDdl = false;

        /** Whether history stores DDL for monitored tables only (default {@code false}). */
        private Boolean databaseHistoryStoreOnlyMonitoredTablesDdl = false;

        /** Whether history stores DDL for captured tables only (default {@code false}). */
        private Boolean databaseHistoryStoreOnlyCapturedTablesDdl = false;

        /** Number of Kafka history recovery attempts (default {@code 4}). */
        private Integer databaseHistoryKafkaRecoveryAttempts = 4;

        /** Poll interval (ms) for Kafka history recovery (default {@code 100}). */
        private Integer databaseHistoryKafkaRecoveryPollIntervalMs = 100;
    }

    /** PostgreSQL connector specific tuning. */
    @Data
    public static class PostgreSql {
        /** Logical replication plugin name (default {@code pgoutput}). */
        private String pluginName = "pgoutput";
        /** Logical replication slot name. */
        private String slotName;
        /** Publication name for the {@code pgoutput} plugin. */
        private String publicationName;
        /** Snapshot mode (default {@code initial}). */
        private String snapshotMode = "initial";
        /** SSL mode (default {@code prefer}). */
        private String sslMode = "prefer";
        /** SSL client certificate path. */
        private String sslCert = "";
        /** SSL client private key path. */
        private String sslKey = "";
        /** SSL root certificate path. */
        private String sslRootCert = "";
        /** Password protecting the SSL client key. */
        private String sslPassword = "";
        /** Whether to emit a tombstone event after a delete (default {@code false}). */
        private Boolean tombstonesOnDelete = false;
        /** Whether to include the originating SQL query in events (default {@code false}). */
        private Boolean includeQuery = false;
        /** Time (ms) between polls for new change events (default {@code 1000}). */
        private Integer pollIntervalMs = 1000;
        /** Maximum number of change events queued in the blocking queue (default {@code 8192}). */
        private Integer maxQueueSize = 8192;
        /** Maximum number of change events processed in a single batch (default {@code 2048}). */
        private Integer maxBatchSize = 2048;
    }

    /** MongoDB connector specific tuning. */
    @Data
    public static class MongoDb {
        /** MongoDB connection string (e.g. {@code mongodb://host:port}). */
        private String connectionString;
        /** Comma separated list of databases to capture. */
        private String databaseList;
        /** Comma separated list of collections to capture. */
        private String collectionList;
        /** Snapshot mode (default {@code initial}). */
        private String snapshotMode = "initial";
        /** Authentication source database (default {@code admin}). */
        private String authSource = "admin";
        /** Connection timeout in milliseconds (default {@code 30000}). */
        private Integer connectTimeoutMs = 30000;
        /** Socket timeout in milliseconds (default {@code 30000}). */
        private Integer socketTimeoutMs = 30000;
        /** Server selection timeout in milliseconds (default {@code 30000}). */
        private Integer serverSelectionTimeoutMs = 30000;
        /** Maximum connection pool size (default {@code 100}). */
        private Integer maxConnectionPoolSize = 100;
        /** Minimum connection pool size (default {@code 5}). */
        private Integer minConnectionPoolSize = 5;
        /** Maximum idle time for connections in milliseconds (default {@code 30000}). */
        private Integer maxConnectionIdleTimeMs = 30000;
        /** Maximum lifetime for connections in milliseconds (default {@code 300000}). */
        private Integer maxConnectionLifeTimeMs = 300000;
        /** Whether to emit a tombstone event after a delete (default {@code false}). */
        private Boolean tombstonesOnDelete = false;
        /** Whether to include the originating operation in events (default {@code false}). */
        private Boolean includeQuery = false;
        /** Comma separated field rename mappings. */
        private String fieldRenames = "";
        /** Comma separated list of fields to exclude. */
        private String fieldExcludeList = "";
        /** Time (ms) between polls for new change events (default {@code 1000}). */
        private Integer pollIntervalMs = 1000;
        /** Maximum number of change events queued in the blocking queue (default {@code 8192}). */
        private Integer maxQueueSize = 8192;
        /** Maximum number of change events processed in a single batch (default {@code 2048}). */
        private Integer maxBatchSize = 2048;
        /** Maximum queue size in bytes (default 1 GB). */
        private Long maxQueueSizeInBytes = 1073741824L;
    }

    /** Oracle connector specific tuning. */
    @Data
    public static class Oracle {
        /** Database name (SID or service name). */
        private String database;
        /** Pluggable database (PDB) name. */
        private String pdbName;
        /** Snapshot mode (default {@code initial}). */
        private String snapshotMode = "initial";
        /** Log mining strategy (default {@code online_catalog}). */
        private String logMiningStrategy = "online_catalog";
    }

    /** SQL Server connector specific tuning. */
    @Data
    public static class SqlServer {
        /** Database name. */
        private String database;
        /** Snapshot mode (default {@code initial}). */
        private String snapshotMode = "initial";
        /** Snapshot isolation level (default {@code snapshot}). */
        private String snapshotIsolationMode = "snapshot";
    }

    /** Cassandra connector specific tuning. */
    @Data
    public static class Cassandra {
        /** Cassandra connection string. */
        private String connectionString;
        /** Comma separated list of keyspaces to capture. */
        private String databaseList;
        /** Comma separated list of tables to capture. */
        private String tableList;
        /** Snapshot mode (default {@code initial}). */
        private String snapshotMode = "initial";
        /** Connection timeout in milliseconds (default {@code 30000}). */
        private Integer connectTimeoutMs = 30000;
        /** Read timeout in milliseconds (default {@code 30000}). */
        private Integer readTimeoutMs = 30000;
        /** Whether to emit a tombstone event after a delete (default {@code false}). */
        private Boolean tombstonesOnDelete = false;
        /** Whether to include the originating query in events (default {@code false}). */
        private Boolean includeQuery = false;
        /** Time (ms) between polls for new change events (default {@code 1000}). */
        private Integer pollIntervalMs = 1000;
        /** Maximum number of change events queued in the blocking queue (default {@code 8192}). */
        private Integer maxQueueSize = 8192;
        /** Maximum number of change events processed in a single batch (default {@code 2048}). */
        private Integer maxBatchSize = 2048;
    }

    /** Google Cloud Spanner connector specific tuning. */
    @Data
    public static class Spanner {
        /** Spanner connection string. */
        private String connectionString;
        /** Comma separated list of databases to capture. */
        private String databaseList;
        /** Comma separated list of tables to capture. */
        private String tableList;
        /** Snapshot mode (default {@code initial}). */
        private String snapshotMode = "initial";
        /** Google Cloud project id. */
        private String projectId;
        /** Spanner instance id. */
        private String instanceId;
        /** Spanner database id. */
        private String databaseId;
        /** Whether to emit a tombstone event after a delete (default {@code false}). */
        private Boolean tombstonesOnDelete = false;
        /** Whether to include the originating query in events (default {@code false}). */
        private Boolean includeQuery = false;
        /** Time (ms) between polls for new change events (default {@code 1000}). */
        private Integer pollIntervalMs = 1000;
        /** Maximum number of change events queued in the blocking queue (default {@code 8192}). */
        private Integer maxQueueSize = 8192;
        /** Maximum number of change events processed in a single batch (default {@code 2048}). */
        private Integer maxBatchSize = 2048;
    }

    /** Custom connector configuration block. */
    @Data
    public static class Custom {
        /** Fully qualified class name of the custom {@code SourceConnector} implementation. */
        private String connectorClass;
        /** Additional raw properties passed straight through to the connector. */
        private java.util.Map<String, String> props = new java.util.HashMap<>();
    }
}

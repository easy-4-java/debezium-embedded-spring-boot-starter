package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import io.debezium.embedded.util.PropertyMappers;
import java.util.Objects;

/**
 * {@link ConnectorConfigurer} for the Debezium MySQL connector.
 *
 * <p>Translates {@link DebeziumConnectorProperties} into the connector
 * configuration keys documented by Debezium, including snapshot, GTID,
 * connection, security and performance tuning.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 * @see <a href="https://debezium.io/documentation/reference/3.2/connectors/mysql.html">MySQL Connector Documentation</a>
 */
public class MySqlConnectorConfigurer implements ConnectorConfigurer {

    /**
     * Applies the MySQL connector configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the connector configuration properties
     */
    @Override
    /**
     * <p>Apply.</p>
     * @param builder
     * @param properties
     */
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        PropertyMapper map = PropertyMappers.whenNonNull();
        // ==================== Required configuration ====================
        builder.with("connector.class", "io.debezium.connector.mysql.MySqlConnector");

        // Database connection configuration (required)
        mapRequiredProperties(builder, map, properties);

        // ==================== Optional configuration ====================

        // Database and table filtering
        mapDatabaseAndTableFilters(builder, map, properties);

        // MySQL specific configuration
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();

            // Snapshot configuration
            mapSnapshotConfig(builder, map, mySql);

            // Connection and performance configuration
            mapConnectionAndPerformanceConfig(builder, map, mySql);

            // GTID and replication configuration
            mapGtidAndReplicationConfig(builder, map, mySql);

            // Database connection configuration
            mapDatabaseConnectionConfig(builder, map, mySql);

            // Event processing configuration
            mapEventProcessingConfig(builder, map, mySql);

            // Performance optimisation configuration
            mapPerformanceOptimizationConfig(builder, map, mySql);

            // Security configuration
            mapSecurityConfig(builder, map, mySql);

            // Monitoring and debugging configuration
            mapMonitoringAndDebugConfig(builder, map, mySql);
        }
    }

    /**
     * Maps the required database connection properties.
     */
    private void mapRequiredProperties(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties properties) {
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).when(Objects::nonNull).to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerId).whenHasText().to(value -> builder.with("database.server.id", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
    }
    
    /**
     * Maps the database and table include/exclude filters.
     */
    private void mapDatabaseAndTableFilters(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties properties) {
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with("database.exclude.list", value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with("table.exclude.list", value));
    }
    
    /**
     * Maps the snapshot configuration.
     */
    private void mapSnapshotConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
        map.from(mySql::getSnapshotLockingMode).whenHasText().to(value -> builder.with("snapshot.locking.mode", value));
        map.from(mySql::getSnapshotNewTables).to(value -> builder.with("snapshot.new.tables", value));
        map.from(mySql::getSnapshotDelayMs).to(value -> builder.with("snapshot.delay.ms", value));
        map.from(mySql::getSnapshotFetchSize).to(value -> builder.with("snapshot.fetch.size", value));
    }
    
    /**
     * Maps the connection and performance configuration.
     */
    private void mapConnectionAndPerformanceConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getConnectTimeoutMs).to(value -> builder.with("connect.timeout.ms", value));
        map.from(mySql::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
        map.from(mySql::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
        map.from(mySql::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        map.from(mySql::getMinRowCountToStreamResults).to(value -> builder.with("min.row.count.to.stream.results", value));
    }
    
    /**
     * Maps the GTID and replication configuration.
     */
    private void mapGtidAndReplicationConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getGtidSourceFilterDmlEvents).to(value -> builder.with("gtid.source.filter.dml.events", value));
        map.from(mySql::getGtidSourceIncludeDatabases).whenHasText().to(value -> builder.with("gtid.source.include.databases", value));
        map.from(mySql::getGtidSourceExcludeDatabases).whenHasText().to(value -> builder.with("gtid.source.exclude.databases", value));
        map.from(mySql::getGtidSourceFilterDdlEvents).to(value -> builder.with("gtid.source.filter.ddl.events", value));
    }
    
    /**
     * Maps the JDBC connection configuration.
     */
    private void mapDatabaseConnectionConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getAllowPublicKeyRetrieval).to(value -> builder.with("database.allowPublicKeyRetrieval", value));
        map.from(mySql::getUseSSL).to(value -> builder.with("database.useSSL", value));
        map.from(mySql::getAutoReconnect).to(value -> builder.with("database.autoReconnect", value));
        map.from(mySql::getAllowMultiQueries).to(value -> builder.with("database.allowMultiQueries", value));
        map.from(mySql::getZeroDateTimeBehavior).whenHasText().to(value -> builder.with("database.zeroDateTimeBehavior", value));
        map.from(mySql::getCharacterEncoding).whenHasText().to(value -> builder.with("database.characterEncoding", value));
        map.from(mySql::getUseUnicode).to(value -> builder.with("database.useUnicode", value));
        map.from(mySql::getServerTimezone).whenHasText().to(value -> builder.with("database.serverTimezone", value));
        map.from(mySql::getConnectionTimeZone).whenHasText().to(value -> builder.with("database.connectionTimeZone", value));
    }
    
    /**
     * Maps the event processing configuration.
     */
    private void mapEventProcessingConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
        map.from(mySql::getIncludeQuery).to(value -> builder.with("include.query", value));
        map.from(mySql::getIncludeSchemaChanges).to(value -> builder.with("include.schema.changes", value));
        map.from(mySql::getProvideTransactionMetadata).to(value -> builder.with("provide.transaction.metadata", value));
    }
    
    /**
     * Maps the performance optimisation configuration.
     */
    private void mapPerformanceOptimizationConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getMaxQueueSizeInBytes).to(value -> builder.with("max.queue.size.in.bytes", value));
        map.from(mySql::getIncrementalSnapshotChunkSize).to(value -> builder.with("incremental.snapshot.chunk.size", value));
        map.from(mySql::getIncrementalSnapshotAllowSchemaChanges).to(value -> builder.with("incremental.snapshot.allow.schema.changes", value));
        map.from(mySql::getSignalDataCollection).whenHasText().to(value -> builder.with("signal.data.collection", value));
    }
    
    /**
     * Maps the SSL security configuration.
     */
    private void mapSecurityConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getSslMode).whenHasText().to(value -> builder.with("database.ssl.mode", value));
        map.from(mySql::getSslTruststore).whenHasText().to(value -> builder.with("database.ssl.truststore", value));
        map.from(mySql::getSslTruststorePassword).whenHasText().to(value -> builder.with("database.ssl.truststore.password", value));
        map.from(mySql::getSslKeystore).whenHasText().to(value -> builder.with("database.ssl.keystore", value));
        map.from(mySql::getSslKeystorePassword).whenHasText().to(value -> builder.with("database.ssl.keystore.password", value));
    }
    
    /**
     * Maps the schema history monitoring and debugging configuration.
     */
    private void mapMonitoringAndDebugConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getDatabaseHistorySkipUnparseableDdl).to(value -> builder.with("database.history.skip.unparseable.ddl", value));
        map.from(mySql::getDatabaseHistoryStoreOnlyMonitoredTablesDdl).to(value -> builder.with("database.history.store.only.monitored.tables.ddl", value));
        map.from(mySql::getDatabaseHistoryStoreOnlyCapturedTablesDdl).to(value -> builder.with("database.history.store.only.captured.tables.ddl", value));
        map.from(mySql::getDatabaseHistoryKafkaRecoveryAttempts).to(value -> builder.with("database.history.kafka.recovery.attempts", value));
        map.from(mySql::getDatabaseHistoryKafkaRecoveryPollIntervalMs).to(value -> builder.with("database.history.kafka.recovery.poll.interval.ms", value));
    }
}



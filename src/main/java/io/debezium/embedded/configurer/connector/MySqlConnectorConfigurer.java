package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MySQL 连接器配置器。
 * 
 * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium MySQL 连接器配置，
 * 严格按照官方文档中的参数名称进行映射。</p>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/connectors/mysql.html">MySQL Connector Documentation</a>
 */
public class MySqlConnectorConfigurer implements ConnectorConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        // ==================== 必需配置 ====================
        builder.with("connector.class", "io.debezium.connector.mysql.MySqlConnector");

        // 数据库连接配置（必需）
        mapRequiredProperties(builder, map, properties);
        
        // ==================== 批量设置可选参数 ====================
        
        // 数据库和表过滤配置
        mapDatabaseAndTableFilters(builder, map, properties);
        
        // MySQL 特定配置
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();
            
            // 快照配置
            mapSnapshotConfig(builder, map, mySql);
            
            // 连接和性能配置
            mapConnectionAndPerformanceConfig(builder, map, mySql);
            
            // GTID 和复制配置
            mapGtidAndReplicationConfig(builder, map, mySql);
            
            // 数据库连接配置
            mapDatabaseConnectionConfig(builder, map, mySql);
            
            // 事件处理配置
            mapEventProcessingConfig(builder, map, mySql);
            
            // 性能优化配置
            mapPerformanceOptimizationConfig(builder, map, mySql);
            
            // 安全配置
            mapSecurityConfig(builder, map, mySql);
            
            // 监控和调试配置
            mapMonitoringAndDebugConfig(builder, map, mySql);
        }
    }
    
    /**
     * 映射必需配置属性
     */
    private void mapRequiredProperties(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties properties) {
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerId).whenHasText().to(value -> builder.with("database.server.id", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
    }
    
    /**
     * 映射数据库和表过滤配置
     */
    private void mapDatabaseAndTableFilters(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties properties) {
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with("database.exclude.list", value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with("table.exclude.list", value));
    }
    
    /**
     * 映射快照配置
     */
    private void mapSnapshotConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
        map.from(mySql::getSnapshotLockingMode).whenHasText().to(value -> builder.with("snapshot.locking.mode", value));
        map.from(mySql::getSnapshotNewTables).to(value -> builder.with("snapshot.new.tables", value));
        map.from(mySql::getSnapshotDelayMs).to(value -> builder.with("snapshot.delay.ms", value));
        map.from(mySql::getSnapshotFetchSize).to(value -> builder.with("snapshot.fetch.size", value));
    }
    
    /**
     * 映射连接和性能配置
     */
    private void mapConnectionAndPerformanceConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getConnectTimeoutMs).to(value -> builder.with("connect.timeout.ms", value));
        map.from(mySql::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
        map.from(mySql::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
        map.from(mySql::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        map.from(mySql::getMinRowCountToStreamResults).to(value -> builder.with("min.row.count.to.stream.results", value));
    }
    
    /**
     * 映射 GTID 和复制配置
     */
    private void mapGtidAndReplicationConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getGtidSourceFilterDmlEvents).to(value -> builder.with("gtid.source.filter.dml.events", value));
        map.from(mySql::getGtidSourceIncludeDatabases).whenHasText().to(value -> builder.with("gtid.source.include.databases", value));
        map.from(mySql::getGtidSourceExcludeDatabases).whenHasText().to(value -> builder.with("gtid.source.exclude.databases", value));
        map.from(mySql::getGtidSourceFilterDdlEvents).to(value -> builder.with("gtid.source.filter.ddl.events", value));
    }
    
    /**
     * 映射数据库连接配置
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
     * 映射事件处理配置
     */
    private void mapEventProcessingConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
        map.from(mySql::getIncludeQuery).to(value -> builder.with("include.query", value));
        map.from(mySql::getIncludeSchemaChanges).to(value -> builder.with("include.schema.changes", value));
        map.from(mySql::getProvideTransactionMetadata).to(value -> builder.with("provide.transaction.metadata", value));
    }
    
    /**
     * 映射性能优化配置
     */
    private void mapPerformanceOptimizationConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getMaxQueueSizeInBytes).to(value -> builder.with("max.queue.size.in.bytes", value));
        map.from(mySql::getIncrementalSnapshotChunkSize).to(value -> builder.with("incremental.snapshot.chunk.size", value));
        map.from(mySql::getIncrementalSnapshotAllowSchemaChanges).to(value -> builder.with("incremental.snapshot.allow.schema.changes", value));
        map.from(mySql::getSignalDataCollection).whenHasText().to(value -> builder.with("signal.data.collection", value));
    }
    
    /**
     * 映射安全配置
     */
    private void mapSecurityConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getSslMode).whenHasText().to(value -> builder.with("database.ssl.mode", value));
        map.from(mySql::getSslTruststore).whenHasText().to(value -> builder.with("database.ssl.truststore", value));
        map.from(mySql::getSslTruststorePassword).whenHasText().to(value -> builder.with("database.ssl.truststore.password", value));
        map.from(mySql::getSslKeystore).whenHasText().to(value -> builder.with("database.ssl.keystore", value));
        map.from(mySql::getSslKeystorePassword).whenHasText().to(value -> builder.with("database.ssl.keystore.password", value));
    }
    
    /**
     * 映射监控和调试配置
     */
    private void mapMonitoringAndDebugConfig(Configuration.Builder builder, PropertyMapper map, DebeziumConnectorProperties.MySql mySql) {
        map.from(mySql::getDatabaseHistorySkipUnparseableDdl).to(value -> builder.with("database.history.skip.unparseable.ddl", value));
        map.from(mySql::getDatabaseHistoryStoreOnlyMonitoredTablesDdl).to(value -> builder.with("database.history.store.only.monitored.tables.ddl", value));
        map.from(mySql::getDatabaseHistoryStoreOnlyCapturedTablesDdl).to(value -> builder.with("database.history.store.only.captured.tables.ddl", value));
        map.from(mySql::getDatabaseHistoryKafkaRecoveryAttempts).to(value -> builder.with("database.history.kafka.recovery.attempts", value));
        map.from(mySql::getDatabaseHistoryKafkaRecoveryPollIntervalMs).to(value -> builder.with("database.history.kafka.recovery.poll.interval.ms", value));
    }
}



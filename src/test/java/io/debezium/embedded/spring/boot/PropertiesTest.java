package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.connector.ConnectorType;
import io.debezium.embedded.configurer.history.SchemaHistoryType;
import io.debezium.embedded.configurer.storage.OffsetStorageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for all Spring Boot configuration properties classes.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("Properties classes")
class PropertiesTest {

    // ==================== DebeziumEmbeddedProperties ====================

    @Test
    @DisplayName("DebeziumEmbeddedProperties defaults")
    void debeziumEmbeddedProperties_defaults() {
        DebeziumEmbeddedProperties props = new DebeziumEmbeddedProperties();
        assertThat(DebeziumEmbeddedProperties.PREFIX).isEqualTo("debezium");
        assertThat(props.getInstances()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("DebeziumEmbeddedProperties.Instance defaults")
    void debeziumEmbeddedPropertiesInstance_defaults() {
        DebeziumEmbeddedProperties.Instance instance = new DebeziumEmbeddedProperties.Instance();
        assertThat(instance.getEventType()).isEqualTo(DebeziumEmbeddedProperties.EventType.CHANGE_EVENT);
        assertThat(instance.getAsync()).isNotNull();
        assertThat(instance.getConnector()).isNotNull();
        assertThat(instance.getSchemaHistory()).isNotNull();
        assertThat(instance.getOffsetStorage()).isNotNull();
    }

    @Test
    @DisplayName("DebeziumEmbeddedProperties.Instance setters/getters")
    void debeziumEmbeddedPropertiesInstance_setterGetter() {
        DebeziumEmbeddedProperties.Instance instance = new DebeziumEmbeddedProperties.Instance();
        instance.setEventType(DebeziumEmbeddedProperties.EventType.RECORD_CHANGE_EVENT);
        assertThat(instance.getEventType()).isEqualTo(DebeziumEmbeddedProperties.EventType.RECORD_CHANGE_EVENT);
    }

    @Test
    @DisplayName("DebeziumEmbeddedProperties.EventType values")
    void debeziumEmbeddedPropertiesEventType_values() {
        assertThat(DebeziumEmbeddedProperties.EventType.values()).hasSize(2);
        assertThat(DebeziumEmbeddedProperties.EventType.CHANGE_EVENT).isNotNull();
        assertThat(DebeziumEmbeddedProperties.EventType.RECORD_CHANGE_EVENT).isNotNull();
    }

    // ==================== DebeziumAsyncEngineProperties ====================

    @Test
    @DisplayName("DebeziumAsyncEngineProperties defaults")
    void asyncEngineProperties_defaults() {
        DebeziumAsyncEngineProperties props = new DebeziumAsyncEngineProperties();
        assertThat(props.getThreads()).isEqualTo(Runtime.getRuntime().availableProcessors());
        assertThat(props.getShutdownTimeoutMs()).isEqualTo(1000);
        assertThat(props.getOrder()).isEqualTo(DebeziumAsyncEngineProperties.Order.ORDERED);
        assertThat(props.isWithSerialConsumer()).isFalse();
        assertThat(props.getTimeoutMs()).isEqualTo(180000);
    }

    @Test
    @DisplayName("DebeziumAsyncEngineProperties setters/getters")
    void asyncEngineProperties_setterGetter() {
        DebeziumAsyncEngineProperties props = new DebeziumAsyncEngineProperties();
        props.setThreads(4);
        props.setShutdownTimeoutMs(5000);
        props.setOrder(DebeziumAsyncEngineProperties.Order.UNORDERED);
        props.setWithSerialConsumer(true);
        props.setTimeoutMs(300000);

        assertThat(props.getThreads()).isEqualTo(4);
        assertThat(props.getShutdownTimeoutMs()).isEqualTo(5000);
        assertThat(props.getOrder()).isEqualTo(DebeziumAsyncEngineProperties.Order.UNORDERED);
        assertThat(props.isWithSerialConsumer()).isTrue();
        assertThat(props.getTimeoutMs()).isEqualTo(300000);
    }

    @Test
    @DisplayName("DebeziumAsyncEngineProperties.Order values")
    void asyncEngineOrder_values() {
        assertThat(DebeziumAsyncEngineProperties.Order.values()).hasSize(2);
        assertThat(DebeziumAsyncEngineProperties.Order.ORDERED).isNotNull();
        assertThat(DebeziumAsyncEngineProperties.Order.UNORDERED).isNotNull();
    }

    // ==================== DebeziumThreadPoolProperties ====================

    @Test
    @DisplayName("DebeziumThreadPoolProperties defaults")
    void threadPoolProperties_defaults() {
        DebeziumThreadPoolProperties props = new DebeziumThreadPoolProperties();
        assertThat(DebeziumThreadPoolProperties.PREFIX).isEqualTo("debezium.thread-pool");
        assertThat(props.getCorePoolSize()).isEqualTo(1);
        assertThat(props.getMaxPoolSize()).isEqualTo(Runtime.getRuntime().availableProcessors());
        assertThat(props.getQueueCapacity()).isEqualTo(Integer.MAX_VALUE);
        assertThat(props.getKeepAlive()).isEqualTo(Duration.ofSeconds(60));
        assertThat(props.isAllowCoreThreadTimeOut()).isFalse();
        assertThat(props.isWaitForTasksToCompleteOnShutdown()).isFalse();
        assertThat(props.getAwaitTerminationSeconds()).isEqualTo(0);
        assertThat(props.getThreadNamePrefix()).isEqualTo("RedisAsyncTaskExecutor-");
        assertThat(props.isDaemon()).isFalse();
        assertThat(props.getRejectedPolicy()).isEqualTo(DebeziumThreadPoolProperties.RejectedPolicy.AbortPolicy);
    }

    @Test
    @DisplayName("DebeziumThreadPoolProperties setters/getters")
    void threadPoolProperties_setterGetter() {
        DebeziumThreadPoolProperties props = new DebeziumThreadPoolProperties();
        props.setCorePoolSize(2);
        props.setMaxPoolSize(8);
        props.setQueueCapacity(100);
        props.setKeepAlive(Duration.ofSeconds(120));
        props.setAllowCoreThreadTimeOut(true);
        props.setWaitForTasksToCompleteOnShutdown(true);
        props.setAwaitTerminationSeconds(30);
        props.setThreadNamePrefix("my-thread-");
        props.setDaemon(true);
        props.setRejectedPolicy(DebeziumThreadPoolProperties.RejectedPolicy.CallerRunsPolicy);

        assertThat(props.getCorePoolSize()).isEqualTo(2);
        assertThat(props.getMaxPoolSize()).isEqualTo(8);
        assertThat(props.getQueueCapacity()).isEqualTo(100);
        assertThat(props.getKeepAlive()).isEqualTo(Duration.ofSeconds(120));
        assertThat(props.isAllowCoreThreadTimeOut()).isTrue();
        assertThat(props.isWaitForTasksToCompleteOnShutdown()).isTrue();
        assertThat(props.getAwaitTerminationSeconds()).isEqualTo(30);
        assertThat(props.getThreadNamePrefix()).isEqualTo("my-thread-");
        assertThat(props.isDaemon()).isTrue();
        assertThat(props.getRejectedPolicy()).isEqualTo(DebeziumThreadPoolProperties.RejectedPolicy.CallerRunsPolicy);
    }

    @Test
    @DisplayName("DebeziumThreadPoolProperties.RejectedPolicy values and handlers")
    void rejectedPolicy_valuesAndHandlers() {
        assertThat(DebeziumThreadPoolProperties.RejectedPolicy.values()).hasSize(4);
        for (DebeziumThreadPoolProperties.RejectedPolicy policy : DebeziumThreadPoolProperties.RejectedPolicy.values()) {
            assertThat(policy.getRejectedExecutionHandler()).isNotNull();
        }
    }

    // ==================== DebeziumConnectorProperties ====================

    @Test
    @DisplayName("DebeziumConnectorProperties defaults")
    void connectorProperties_defaults() {
        DebeziumConnectorProperties props = new DebeziumConnectorProperties();
        assertThat(props.getType()).isEqualTo(ConnectorType.MYSQL);
        assertThat(props.getMySql()).isNotNull();
        assertThat(props.getPostgreSql()).isNotNull();
        assertThat(props.getMongoDb()).isNotNull();
        assertThat(props.getOracle()).isNotNull();
        assertThat(props.getSqlServer()).isNotNull();
        assertThat(props.getCassandra()).isNotNull();
        assertThat(props.getSpanner()).isNotNull();
        assertThat(props.getCustom()).isNotNull();
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.MySql defaults")
    void mySqlDefaults() {
        DebeziumConnectorProperties.MySql mySql = new DebeziumConnectorProperties.MySql();
        assertThat(mySql.getSnapshotMode()).isEqualTo("initial");
        assertThat(mySql.getSnapshotLockingMode()).isEqualTo("minimal");
        assertThat(mySql.getSnapshotNewTables()).isFalse();
        assertThat(mySql.getSnapshotDelayMs()).isEqualTo(0L);
        assertThat(mySql.getSnapshotFetchSize()).isEqualTo(1024);
        assertThat(mySql.getConnectTimeoutMs()).isEqualTo(30000);
        assertThat(mySql.getPollIntervalMs()).isEqualTo(1000);
        assertThat(mySql.getMaxQueueSize()).isEqualTo(8192);
        assertThat(mySql.getMaxBatchSize()).isEqualTo(2048);
        assertThat(mySql.getMinRowCountToStreamResults()).isEqualTo(1000);
        assertThat(mySql.getMaxQueueSizeInBytes()).isEqualTo(1073741824L);
        assertThat(mySql.getGtidSourceFilterDmlEvents()).isTrue();
        assertThat(mySql.getGtidSourceFilterDdlEvents()).isFalse();
        assertThat(mySql.getAllowPublicKeyRetrieval()).isTrue();
        assertThat(mySql.getUseSSL()).isFalse();
        assertThat(mySql.getAutoReconnect()).isTrue();
        assertThat(mySql.getAllowMultiQueries()).isTrue();
        assertThat(mySql.getZeroDateTimeBehavior()).isEqualTo("convertToNull");
        assertThat(mySql.getCharacterEncoding()).isEqualTo("utf8");
        assertThat(mySql.getUseUnicode()).isTrue();
        assertThat(mySql.getTombstonesOnDelete()).isFalse();
        assertThat(mySql.getIncludeQuery()).isFalse();
        assertThat(mySql.getIncludeSchemaChanges()).isTrue();
        assertThat(mySql.getProvideTransactionMetadata()).isFalse();
        assertThat(mySql.getIncrementalSnapshotChunkSize()).isEqualTo(1024);
        assertThat(mySql.getIncrementalSnapshotAllowSchemaChanges()).isTrue();
        assertThat(mySql.getSslMode()).isEqualTo("disabled");
        assertThat(mySql.getDatabaseHistorySkipUnparseableDdl()).isFalse();
        assertThat(mySql.getDatabaseHistoryStoreOnlyMonitoredTablesDdl()).isFalse();
        assertThat(mySql.getDatabaseHistoryStoreOnlyCapturedTablesDdl()).isFalse();
        assertThat(mySql.getDatabaseHistoryKafkaRecoveryAttempts()).isEqualTo(4);
        assertThat(mySql.getDatabaseHistoryKafkaRecoveryPollIntervalMs()).isEqualTo(100);
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.PostgreSql defaults")
    void postgreSqlDefaults() {
        DebeziumConnectorProperties.PostgreSql pg = new DebeziumConnectorProperties.PostgreSql();
        assertThat(pg.getPluginName()).isEqualTo("pgoutput");
        assertThat(pg.getSnapshotMode()).isEqualTo("initial");
        assertThat(pg.getSslMode()).isEqualTo("prefer");
        assertThat(pg.getSslCert()).isEmpty();
        assertThat(pg.getSslKey()).isEmpty();
        assertThat(pg.getSslRootCert()).isEmpty();
        assertThat(pg.getSslPassword()).isEmpty();
        assertThat(pg.getTombstonesOnDelete()).isFalse();
        assertThat(pg.getIncludeQuery()).isFalse();
        assertThat(pg.getPollIntervalMs()).isEqualTo(1000);
        assertThat(pg.getMaxQueueSize()).isEqualTo(8192);
        assertThat(pg.getMaxBatchSize()).isEqualTo(2048);
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.MongoDb defaults")
    void mongoDbDefaults() {
        DebeziumConnectorProperties.MongoDb mongo = new DebeziumConnectorProperties.MongoDb();
        assertThat(mongo.getSnapshotMode()).isEqualTo("initial");
        assertThat(mongo.getAuthSource()).isEqualTo("admin");
        assertThat(mongo.getConnectTimeoutMs()).isEqualTo(30000);
        assertThat(mongo.getSocketTimeoutMs()).isEqualTo(30000);
        assertThat(mongo.getServerSelectionTimeoutMs()).isEqualTo(30000);
        assertThat(mongo.getMaxConnectionPoolSize()).isEqualTo(100);
        assertThat(mongo.getMinConnectionPoolSize()).isEqualTo(5);
        assertThat(mongo.getMaxConnectionIdleTimeMs()).isEqualTo(30000);
        assertThat(mongo.getMaxConnectionLifeTimeMs()).isEqualTo(300000);
        assertThat(mongo.getTombstonesOnDelete()).isFalse();
        assertThat(mongo.getIncludeQuery()).isFalse();
        assertThat(mongo.getFieldRenames()).isEmpty();
        assertThat(mongo.getFieldExcludeList()).isEmpty();
        assertThat(mongo.getPollIntervalMs()).isEqualTo(1000);
        assertThat(mongo.getMaxQueueSize()).isEqualTo(8192);
        assertThat(mongo.getMaxBatchSize()).isEqualTo(2048);
        assertThat(mongo.getMaxQueueSizeInBytes()).isEqualTo(1073741824L);
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.Oracle defaults")
    void oracleDefaults() {
        DebeziumConnectorProperties.Oracle oracle = new DebeziumConnectorProperties.Oracle();
        assertThat(oracle.getSnapshotMode()).isEqualTo("initial");
        assertThat(oracle.getLogMiningStrategy()).isEqualTo("online_catalog");
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.SqlServer defaults")
    void sqlServerDefaults() {
        DebeziumConnectorProperties.SqlServer sql = new DebeziumConnectorProperties.SqlServer();
        assertThat(sql.getSnapshotMode()).isEqualTo("initial");
        assertThat(sql.getSnapshotIsolationMode()).isEqualTo("snapshot");
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.Cassandra defaults")
    void cassandraDefaults() {
        DebeziumConnectorProperties.Cassandra c = new DebeziumConnectorProperties.Cassandra();
        assertThat(c.getSnapshotMode()).isEqualTo("initial");
        assertThat(c.getConnectTimeoutMs()).isEqualTo(30000);
        assertThat(c.getReadTimeoutMs()).isEqualTo(30000);
        assertThat(c.getTombstonesOnDelete()).isFalse();
        assertThat(c.getIncludeQuery()).isFalse();
        assertThat(c.getPollIntervalMs()).isEqualTo(1000);
        assertThat(c.getMaxQueueSize()).isEqualTo(8192);
        assertThat(c.getMaxBatchSize()).isEqualTo(2048);
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.Spanner defaults")
    void spannerDefaults() {
        DebeziumConnectorProperties.Spanner s = new DebeziumConnectorProperties.Spanner();
        assertThat(s.getSnapshotMode()).isEqualTo("initial");
        assertThat(s.getTombstonesOnDelete()).isFalse();
        assertThat(s.getIncludeQuery()).isFalse();
        assertThat(s.getPollIntervalMs()).isEqualTo(1000);
        assertThat(s.getMaxQueueSize()).isEqualTo(8192);
        assertThat(s.getMaxBatchSize()).isEqualTo(2048);
    }

    @Test
    @DisplayName("DebeziumConnectorProperties.Custom defaults")
    void customConnectorDefaults() {
        DebeziumConnectorProperties.Custom c = new DebeziumConnectorProperties.Custom();
        assertThat(c.getProps()).isNotNull().isEmpty();
    }

    // ==================== DebeziumSchemaHistoryProperties ====================

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties defaults")
    void schemaHistoryProperties_defaults() {
        DebeziumSchemaHistoryProperties props = new DebeziumSchemaHistoryProperties();
        assertThat(props.getType()).isEqualTo(SchemaHistoryType.FILE);
        assertThat(props.getFile()).isNotNull();
        assertThat(props.getKafka()).isNotNull();
        assertThat(props.getJdbc()).isNotNull();
        assertThat(props.getRedis()).isNotNull();
        assertThat(props.getS3()).isNotNull();
        assertThat(props.getRocketMq()).isNotNull();
        assertThat(props.getAzureBlob()).isNotNull();
        assertThat(props.getCustom()).isNotNull();
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Kafka defaults")
    void schemaHistoryKafka_defaults() {
        DebeziumSchemaHistoryProperties.Kafka k = new DebeziumSchemaHistoryProperties.Kafka();
        assertThat(k.getRecoveryPollIntervalMs()).isEqualTo(100);
        assertThat(k.getRecoveryAttempts()).isEqualTo(100);
        assertThat(k.getQueryTimeoutMs()).isEqualTo(3);
        assertThat(k.getCreateTimeoutMs()).isEqualTo(30);
        assertThat(k.getProducer()).isNotNull();
        assertThat(k.getConsumer()).isNotNull();
        assertThat(k.getSecurity()).isNotNull();
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Kafka.Producer defaults")
    void schemaHistoryKafkaProducer_defaults() {
        DebeziumSchemaHistoryProperties.Kafka.Producer p = new DebeziumSchemaHistoryProperties.Kafka.Producer();
        assertThat(p.getAcks()).isEqualTo("all");
        assertThat(p.getRetries()).isEqualTo(3);
        assertThat(p.getBatchSize()).isEqualTo(16384);
        assertThat(p.getLingerMs()).isEqualTo(1);
        assertThat(p.getBufferMemory()).isEqualTo(33554432);
        assertThat(p.getCompressionType()).isEqualTo("gzip");
        assertThat(p.getMaxRequestSize()).isEqualTo(1048576);
        assertThat(p.getRequestTimeoutMs()).isEqualTo(30000);
        assertThat(p.getMetadataMaxAgeMs()).isEqualTo(300000);
        assertThat(p.getConnectionsMaxIdleMs()).isEqualTo(540000);
        assertThat(p.getReconnectBackoffMs()).isEqualTo(50);
        assertThat(p.getRetryBackoffMs()).isEqualTo(100);
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Kafka.Consumer defaults")
    void schemaHistoryKafkaConsumer_defaults() {
        DebeziumSchemaHistoryProperties.Kafka.Consumer c = new DebeziumSchemaHistoryProperties.Kafka.Consumer();
        assertThat(c.getAutoOffsetReset()).isEqualTo("earliest");
        assertThat(c.getEnableAutoCommit()).isFalse();
        assertThat(c.getSessionTimeoutMs()).isEqualTo(30000);
        assertThat(c.getHeartbeatIntervalMs()).isEqualTo(3000);
        assertThat(c.getMaxPollRecords()).isEqualTo(500);
        assertThat(c.getMaxPollIntervalMs()).isEqualTo(300000);
        assertThat(c.getRequestTimeoutMs()).isEqualTo(30000);
        assertThat(c.getFetchMinBytes()).isEqualTo(1);
        assertThat(c.getFetchMaxWaitMs()).isEqualTo(500);
        assertThat(c.getConnectionsMaxIdleMs()).isEqualTo(540000);
        assertThat(c.getReconnectBackoffMs()).isEqualTo(50);
        assertThat(c.getRetryBackoffMs()).isEqualTo(100);
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Kafka.Security defaults")
    void schemaHistoryKafkaSecurity_defaults() {
        DebeziumSchemaHistoryProperties.Kafka.Security s = new DebeziumSchemaHistoryProperties.Kafka.Security();
        assertThat(s.getSecurityProtocol()).isEqualTo("PLAINTEXT");
        assertThat(s.getSslEndpointIdentificationAlgorithm()).isEqualTo("https");
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Jdbc defaults")
    void schemaHistoryJdbc_defaults() {
        DebeziumSchemaHistoryProperties.Jdbc j = new DebeziumSchemaHistoryProperties.Jdbc();
        assertThat(j.getTableName()).isEqualTo("database_history");
        assertThat(j.getPoolSize()).isEqualTo(10);
        assertThat(j.getConnectionTimeout()).isEqualTo(30000);
        assertThat(j.getQueryTimeout()).isEqualTo(30000);
        assertThat(j.getMaxConnectionLifetime()).isEqualTo(1800000);
        assertThat(j.getMaxConnectionIdleTime()).isEqualTo(600000);
        assertThat(j.getMinConnections()).isEqualTo(1);
        assertThat(j.getMaxConnections()).isEqualTo(20);
        assertThat(j.getConnectionValidationQuery()).isEqualTo("SELECT 1");
        assertThat(j.getConnectionValidationTimeout()).isEqualTo(5000);
        assertThat(j.getLeakDetectionThreshold()).isFalse();
        assertThat(j.getLeakDetectionThresholdMs()).isEqualTo(60000);
        assertThat(j.getAutoCommit()).isTrue();
        assertThat(j.getTransactionIsolation()).isEqualTo("TRANSACTION_READ_COMMITTED");
        assertThat(j.getUseSSL()).isFalse();
        assertThat(j.getSslMode()).isEqualTo("PREFERRED");
        assertThat(j.getVerifyServerCertificate()).isTrue();
        assertThat(j.getAllowPublicKeyRetrieval()).isFalse();
        assertThat(j.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(j.getTimezone()).isEqualTo("UTC");
        assertThat(j.getMaxRetries()).isEqualTo(3);
        assertThat(j.getRetryDelayMs()).isEqualTo(1000);
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Redis defaults")
    void schemaHistoryRedis_defaults() {
        DebeziumSchemaHistoryProperties.Redis r = new DebeziumSchemaHistoryProperties.Redis();
        assertThat(r.getKey()).isEqualTo("metadata:debezium:schema_history");
        assertThat(r.getDbIndex()).isEqualTo(0);
        assertThat(r.getSslEnabled()).isFalse();
        assertThat(r.getSslHostnameVerificationEnabled()).isFalse();
        assertThat(r.getSslTruststoreType()).isEqualTo("JKS");
        assertThat(r.getSslKeystoreType()).isEqualTo("JKS");
        assertThat(r.getConnectionTimeoutMs()).isEqualTo(2000);
        assertThat(r.getSocketTimeoutMs()).isEqualTo(2000);
        assertThat(r.getRetryInitialDelayMs()).isEqualTo(300);
        assertThat(r.getRetryMaxDelayMs()).isEqualTo(10000);
        assertThat(r.getRetryMaxAttempts()).isEqualTo(10);
        assertThat(r.getWaitEnabled()).isFalse();
        assertThat(r.getWaitTimeoutMs()).isEqualTo(1000);
        assertThat(r.getWaitRetryEnabled()).isFalse();
        assertThat(r.getWaitRetryDelayMs()).isEqualTo(1000);
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.S3 defaults")
    void schemaHistoryS3_defaults() {
        DebeziumSchemaHistoryProperties.S3 s3 = new DebeziumSchemaHistoryProperties.S3();
        assertThat(s3.getKeyPrefix()).isEqualTo("debezium/history/");
        assertThat(s3.getConnectionTimeout()).isEqualTo(30000);
        assertThat(s3.getReadTimeout()).isEqualTo(30000);
        assertThat(s3.getMaxRetries()).isEqualTo(3);
        assertThat(s3.getRetryDelayMs()).isEqualTo(1000);
        assertThat(s3.getPathStyleAccessEnabled()).isFalse();
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.RocketMq defaults")
    void schemaHistoryRocketMq_defaults() {
        DebeziumSchemaHistoryProperties.RocketMq rmq = new DebeziumSchemaHistoryProperties.RocketMq();
        assertThat(rmq.getAclEnabled()).isFalse();
    }

    @Test
    @DisplayName("DebeziumSchemaHistoryProperties.Custom defaults")
    void schemaHistoryCustom_defaults() {
        DebeziumSchemaHistoryProperties.Custom c = new DebeziumSchemaHistoryProperties.Custom();
        assertThat(c.getProps()).isNotNull().isEmpty();
    }

    // ==================== DebeziumOffsetStorageProperties ====================

    @Test
    @DisplayName("DebeziumOffsetStorageProperties defaults")
    void offsetStorageProperties_defaults() {
        DebeziumOffsetStorageProperties props = new DebeziumOffsetStorageProperties();
        assertThat(props.getType()).isEqualTo(OffsetStorageType.FILE);
        assertThat(props.getFile()).isNotNull();
        assertThat(props.getKafka()).isNotNull();
        assertThat(props.getJdbc()).isNotNull();
        assertThat(props.getRedis()).isNotNull();
        assertThat(props.getCustom()).isNotNull();
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.File defaults")
    void offsetStorageFile_defaults() {
        DebeziumOffsetStorageProperties.File f = new DebeziumOffsetStorageProperties.File();
        assertThat(f.getFileName()).isEqualTo("/tmp/offsets.dat");
        assertThat(f.getFlushIntervalMs()).isEqualTo(60000);
        assertThat(f.getFlushTimeoutMs()).isEqualTo(5000);
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Kafka defaults")
    void offsetStorageKafka_defaults() {
        DebeziumOffsetStorageProperties.Kafka k = new DebeziumOffsetStorageProperties.Kafka();
        assertThat(k.getTopic()).isEqualTo("debezium-offsets");
        assertThat(k.getPartitions()).isEqualTo(25);
        assertThat(k.getReplicationFactor()).isEqualTo(3);
        assertThat(k.getProducer()).isNotNull();
        assertThat(k.getConsumer()).isNotNull();
        assertThat(k.getSecurity()).isNotNull();
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Kafka.Producer defaults")
    void offsetStorageKafkaProducer_defaults() {
        DebeziumOffsetStorageProperties.Kafka.Producer p = new DebeziumOffsetStorageProperties.Kafka.Producer();
        assertThat(p.getAcks()).isEqualTo("all");
        assertThat(p.getRetries()).isEqualTo(3);
        assertThat(p.getBatchSize()).isEqualTo(16384);
        assertThat(p.getLingerMs()).isEqualTo(1);
        assertThat(p.getBufferMemory()).isEqualTo(33554432);
        assertThat(p.getCompressionType()).isEqualTo("gzip");
        assertThat(p.getMaxRequestSize()).isEqualTo(1048576);
        assertThat(p.getRequestTimeoutMs()).isEqualTo(30000);
        assertThat(p.getMetadataMaxAgeMs()).isEqualTo(300000);
        assertThat(p.getConnectionsMaxIdleMs()).isEqualTo(540000);
        assertThat(p.getReconnectBackoffMs()).isEqualTo(50);
        assertThat(p.getRetryBackoffMs()).isEqualTo(100);
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Kafka.Consumer defaults")
    void offsetStorageKafkaConsumer_defaults() {
        DebeziumOffsetStorageProperties.Kafka.Consumer c = new DebeziumOffsetStorageProperties.Kafka.Consumer();
        assertThat(c.getAutoOffsetReset()).isEqualTo("earliest");
        assertThat(c.getEnableAutoCommit()).isFalse();
        assertThat(c.getSessionTimeoutMs()).isEqualTo(30000);
        assertThat(c.getHeartbeatIntervalMs()).isEqualTo(3000);
        assertThat(c.getMaxPollRecords()).isEqualTo(500);
        assertThat(c.getMaxPollIntervalMs()).isEqualTo(300000);
        assertThat(c.getRequestTimeoutMs()).isEqualTo(30000);
        assertThat(c.getFetchMinBytes()).isEqualTo(1);
        assertThat(c.getFetchMaxWaitMs()).isEqualTo(500);
        assertThat(c.getConnectionsMaxIdleMs()).isEqualTo(540000);
        assertThat(c.getReconnectBackoffMs()).isEqualTo(50);
        assertThat(c.getRetryBackoffMs()).isEqualTo(100);
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Kafka.Security defaults")
    void offsetStorageKafkaSecurity_defaults() {
        DebeziumOffsetStorageProperties.Kafka.Security s = new DebeziumOffsetStorageProperties.Kafka.Security();
        assertThat(s.getSecurityProtocol()).isEqualTo("PLAINTEXT");
        assertThat(s.getSslEndpointIdentificationAlgorithm()).isEqualTo("https");
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Jdbc defaults")
    void offsetStorageJdbc_defaults() {
        DebeziumOffsetStorageProperties.Jdbc j = new DebeziumOffsetStorageProperties.Jdbc();
        assertThat(j.getOffsetStorageTableName()).isEqualTo("debezium_offset_storage");
        assertThat(j.getOffsetStorageMaxRetries()).isEqualTo(5);
        assertThat(j.getOffsetStorageRetryDelayMs()).isEqualTo(3000);
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Redis defaults")
    void offsetStorageRedis_defaults() {
        DebeziumOffsetStorageProperties.Redis r = new DebeziumOffsetStorageProperties.Redis();
        assertThat(r.getKey()).isEqualTo("metadata:debezium:offsets");
        assertThat(r.getDbIndex()).isEqualTo(0);
        assertThat(r.getSslEnabled()).isFalse();
        assertThat(r.getSslHostnameVerificationEnabled()).isFalse();
        assertThat(r.getSslTruststoreType()).isEqualTo("JKS");
        assertThat(r.getSslKeystoreType()).isEqualTo("JKS");
        assertThat(r.getConnectionTimeoutMs()).isEqualTo(2000);
        assertThat(r.getSocketTimeoutMs()).isEqualTo(2000);
        assertThat(r.getRetryInitialDelayMs()).isEqualTo(300);
        assertThat(r.getRetryMaxDelayMs()).isEqualTo(10000);
        assertThat(r.getRetryMaxAttempts()).isEqualTo(10);
        assertThat(r.getWaitEnabled()).isFalse();
        assertThat(r.getWaitTimeoutMs()).isEqualTo(1000);
        assertThat(r.getWaitRetryEnabled()).isFalse();
        assertThat(r.getWaitRetryDelayMs()).isEqualTo(1000);
    }

    @Test
    @DisplayName("DebeziumOffsetStorageProperties.Custom defaults")
    void offsetStorageCustom_defaults() {
        DebeziumOffsetStorageProperties.Custom c = new DebeziumOffsetStorageProperties.Custom();
        assertThat(c.getProps()).isNotNull().isEmpty();
    }
}

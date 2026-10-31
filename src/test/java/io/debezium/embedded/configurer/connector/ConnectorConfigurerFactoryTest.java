package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConnectorConfigurerFactory} and every
 * {@link ConnectorConfigurer} implementation.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("Connector configurers")
class ConnectorConfigurerFactoryTest {

    @Test
    @DisplayName("factory resolves every supported type")
    void from_resolvesAllTypes() {
        for (ConnectorType type : ConnectorType.values()) {
            DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
            properties.setType(type);
            ConnectorConfigurer configurer = ConnectorConfigurerFactory.from(properties);
            assertThat(configurer).as("configurer for %s", type).isNotNull();
        }
    }

    @Test
    @DisplayName("MYSQL configurer maps all keys")
    void mysql_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.MYSQL);
        properties.setHost("localhost");
        properties.setPort(3306);
        properties.setUsername("root");
        properties.setPassword("pass");
        properties.setServerId("1");
        properties.setServerName("myserver");
        properties.setDatabaseIncludeList("db1");
        properties.setTableIncludeList("db1.table1");
        properties.setDatabaseExcludeList("db2");
        properties.setTableExcludeList("db1.table2");

        DebeziumConnectorProperties.MySql mySql = properties.getMySql();
        mySql.setSnapshotMode("schema_only");
        mySql.setSnapshotLockingMode("extended");
        mySql.setSnapshotNewTables(true);
        mySql.setSnapshotDelayMs(5000L);
        mySql.setSnapshotFetchSize(2048);
        mySql.setConnectTimeoutMs(60000);
        mySql.setPollIntervalMs(2000);
        mySql.setMaxQueueSize(16384);
        mySql.setMaxBatchSize(4096);
        mySql.setMinRowCountToStreamResults(2000);
        mySql.setMaxQueueSizeInBytes(2147483648L);
        mySql.setGtidSourceFilterDmlEvents(false);
        mySql.setGtidSourceIncludeDatabases("db1");
        mySql.setGtidSourceExcludeDatabases("db3");
        mySql.setGtidSourceFilterDdlEvents(true);
        mySql.setAllowPublicKeyRetrieval(false);
        mySql.setUseSSL(true);
        mySql.setAutoReconnect(false);
        mySql.setAllowMultiQueries(false);
        mySql.setZeroDateTimeBehavior("exception");
        mySql.setCharacterEncoding("utf8mb4");
        mySql.setUseUnicode(false);
        mySql.setServerTimezone("UTC");
        mySql.setConnectionTimeZone("Asia/Shanghai");
        mySql.setTombstonesOnDelete(true);
        mySql.setIncludeQuery(true);
        mySql.setIncludeSchemaChanges(false);
        mySql.setProvideTransactionMetadata(true);
        mySql.setIncrementalSnapshotChunkSize(2048);
        mySql.setIncrementalSnapshotAllowSchemaChanges(false);
        mySql.setSignalDataCollection("db1.signals");
        mySql.setSslMode("verify_ca");
        mySql.setSslTruststore("/ts.jks");
        mySql.setSslTruststorePassword("tspass");
        mySql.setSslKeystore("/ks.jks");
        mySql.setSslKeystorePassword("kspass");
        mySql.setDatabaseHistorySkipUnparseableDdl(true);
        mySql.setDatabaseHistoryStoreOnlyMonitoredTablesDdl(true);
        mySql.setDatabaseHistoryStoreOnlyCapturedTablesDdl(true);
        mySql.setDatabaseHistoryKafkaRecoveryAttempts(10);
        mySql.setDatabaseHistoryKafkaRecoveryPollIntervalMs(200);

        Configuration config = apply(new MySqlConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.mysql.MySqlConnector");
        assertThat(config.getString("database.hostname")).isEqualTo("localhost");
        assertThat(config.getString("database.port")).isEqualTo("3306");
        assertThat(config.getString("database.user")).isEqualTo("root");
        assertThat(config.getString("database.password")).isEqualTo("pass");
        assertThat(config.getString("database.server.id")).isEqualTo("1");
        assertThat(config.getString("database.server.name")).isEqualTo("myserver");
        assertThat(config.getString("database.include.list")).isEqualTo("db1");
        assertThat(config.getString("table.include.list")).isEqualTo("db1.table1");
        assertThat(config.getString("database.exclude.list")).isEqualTo("db2");
        assertThat(config.getString("table.exclude.list")).isEqualTo("db1.table2");
        assertThat(config.getString("snapshot.mode")).isEqualTo("schema_only");
        assertThat(config.getString("snapshot.locking.mode")).isEqualTo("extended");
        assertThat(config.getString("snapshot.new.tables")).isEqualTo("true");
        assertThat(config.getString("snapshot.delay.ms")).isEqualTo("5000");
        assertThat(config.getString("snapshot.fetch.size")).isEqualTo("2048");
        assertThat(config.getString("connect.timeout.ms")).isEqualTo("60000");
        assertThat(config.getString("poll.interval.ms")).isEqualTo("2000");
        assertThat(config.getString("max.queue.size")).isEqualTo("16384");
        assertThat(config.getString("max.batch.size")).isEqualTo("4096");
        assertThat(config.getString("min.row.count.to.stream.results")).isEqualTo("2000");
        assertThat(config.getString("max.queue.size.in.bytes")).isEqualTo("2147483648");
        assertThat(config.getString("gtid.source.filter.dml.events")).isEqualTo("false");
        assertThat(config.getString("gtid.source.include.databases")).isEqualTo("db1");
        assertThat(config.getString("gtid.source.exclude.databases")).isEqualTo("db3");
        assertThat(config.getString("gtid.source.filter.ddl.events")).isEqualTo("true");
        assertThat(config.getString("database.allowPublicKeyRetrieval")).isEqualTo("false");
        assertThat(config.getString("database.useSSL")).isEqualTo("true");
        assertThat(config.getString("database.autoReconnect")).isEqualTo("false");
        assertThat(config.getString("database.allowMultiQueries")).isEqualTo("false");
        assertThat(config.getString("database.zeroDateTimeBehavior")).isEqualTo("exception");
        assertThat(config.getString("database.characterEncoding")).isEqualTo("utf8mb4");
        assertThat(config.getString("database.useUnicode")).isEqualTo("false");
        assertThat(config.getString("database.serverTimezone")).isEqualTo("UTC");
        assertThat(config.getString("database.connectionTimeZone")).isEqualTo("Asia/Shanghai");
        assertThat(config.getString("tombstones.on.delete")).isEqualTo("true");
        assertThat(config.getString("include.query")).isEqualTo("true");
        assertThat(config.getString("include.schema.changes")).isEqualTo("false");
        assertThat(config.getString("provide.transaction.metadata")).isEqualTo("true");
        assertThat(config.getString("incremental.snapshot.chunk.size")).isEqualTo("2048");
        assertThat(config.getString("incremental.snapshot.allow.schema.changes")).isEqualTo("false");
        assertThat(config.getString("signal.data.collection")).isEqualTo("db1.signals");
        assertThat(config.getString("database.ssl.mode")).isEqualTo("verify_ca");
        assertThat(config.getString("database.ssl.truststore")).isEqualTo("/ts.jks");
        assertThat(config.getString("database.ssl.truststore.password")).isEqualTo("tspass");
        assertThat(config.getString("database.ssl.keystore")).isEqualTo("/ks.jks");
        assertThat(config.getString("database.ssl.keystore.password")).isEqualTo("kspass");
        assertThat(config.getString("database.history.skip.unparseable.ddl")).isEqualTo("true");
        assertThat(config.getString("database.history.store.only.monitored.tables.ddl")).isEqualTo("true");
        assertThat(config.getString("database.history.store.only.captured.tables.ddl")).isEqualTo("true");
        assertThat(config.getString("database.history.kafka.recovery.attempts")).isEqualTo("10");
        assertThat(config.getString("database.history.kafka.recovery.poll.interval.ms")).isEqualTo("200");
    }

    @Test
    @DisplayName("MARIADB configurer sets MySqlConnector with schema.changes=false")
    void mariadb_setsMySqlConnector() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.MARIADB);
        properties.setHost("maria-host");
        properties.setPort(3307);
        properties.setUsername("maria");
        properties.setPassword("mariapass");
        properties.setServerId("2");
        properties.setServerName("mariadb");
        properties.setDatabaseIncludeList("mariadb1");
        properties.setTableIncludeList("mariadb1.t1");
        properties.setDatabaseExcludeList("mariadb2");
        properties.setTableExcludeList("mariadb1.t2");

        DebeziumConnectorProperties.MySql mySql = properties.getMySql();
        mySql.setSnapshotMode("initial");
        mySql.setSnapshotLockingMode("minimal");
        mySql.setConnectTimeoutMs(30000);
        mySql.setGtidSourceFilterDmlEvents(true);

        Configuration config = apply(new MariaDbConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.mysql.MySqlConnector");
        assertThat(config.getString("include.schema.changes")).isEqualTo("false");
        assertThat(config.getString("database.hostname")).isEqualTo("maria-host");
        assertThat(config.getString("database.port")).isEqualTo("3307");
        assertThat(config.getString("database.user")).isEqualTo("maria");
        assertThat(config.getString("database.password")).isEqualTo("mariapass");
        assertThat(config.getString("database.server.id")).isEqualTo("2");
        assertThat(config.getString("database.server.name")).isEqualTo("mariadb");
        assertThat(config.getString("database.include.list")).isEqualTo("mariadb1");
        assertThat(config.getString("table.include.list")).isEqualTo("mariadb1.t1");
        assertThat(config.getString("database.exclude.list")).isEqualTo("mariadb2");
        assertThat(config.getString("table.exclude.list")).isEqualTo("mariadb1.t2");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("snapshot.locking.mode")).isEqualTo("minimal");
        assertThat(config.getString("connect.timeout.ms")).isEqualTo("30000");
        assertThat(config.getString("gtid.source.filter.dml.events")).isEqualTo("true");
    }

    @Test
    @DisplayName("POSTGRESQL configurer maps all postgres keys")
    void postgresql_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.POSTGRESQL);
        properties.setHost("pg-host");
        properties.setPort(5432);
        properties.setUsername("pg");
        properties.setPassword("pgpass");
        properties.setDatabaseName("mydb");
        properties.setServerName("pgserver");
        properties.setDatabaseIncludeList("db1");
        properties.setTableIncludeList("db1.t1");
        properties.setSchemaIncludeList("public");

        DebeziumConnectorProperties.PostgreSql pg = properties.getPostgreSql();
        pg.setPluginName("pgoutput");
        pg.setSlotName("debezium_slot");
        pg.setPublicationName("debezium_pub");
        pg.setSnapshotMode("initial");
        pg.setSslMode("require");
        pg.setSslCert("/cert.pem");
        pg.setSslKey("/key.pem");
        pg.setSslRootCert("/root.pem");
        pg.setSslPassword("sslpass");
        pg.setTombstonesOnDelete(true);
        pg.setIncludeQuery(true);
        pg.setPollIntervalMs(2000);
        pg.setMaxQueueSize(16384);
        pg.setMaxBatchSize(4096);

        Configuration config = apply(new PostgreSqlConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.postgresql.PostgresConnector");
        assertThat(config.getString("database.hostname")).isEqualTo("pg-host");
        assertThat(config.getString("database.port")).isEqualTo("5432");
        assertThat(config.getString("database.user")).isEqualTo("pg");
        assertThat(config.getString("database.password")).isEqualTo("pgpass");
        assertThat(config.getString("database.dbname")).isEqualTo("mydb");
        assertThat(config.getString("database.server.name")).isEqualTo("pgserver");
        assertThat(config.getString("database.include.list")).isEqualTo("db1");
        assertThat(config.getString("table.include.list")).isEqualTo("db1.t1");
        assertThat(config.getString("schema.include.list")).isEqualTo("public");
        assertThat(config.getString("plugin.name")).isEqualTo("pgoutput");
        assertThat(config.getString("slot.name")).isEqualTo("debezium_slot");
        assertThat(config.getString("publication.name")).isEqualTo("debezium_pub");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("database.ssl.mode")).isEqualTo("require");
        assertThat(config.getString("database.ssl.cert")).isEqualTo("/cert.pem");
        assertThat(config.getString("database.ssl.key")).isEqualTo("/key.pem");
        assertThat(config.getString("database.ssl.rootcert")).isEqualTo("/root.pem");
        assertThat(config.getString("database.ssl.password")).isEqualTo("sslpass");
        assertThat(config.getString("tombstones.on.delete")).isEqualTo("true");
        assertThat(config.getString("include.query")).isEqualTo("true");
        assertThat(config.getString("poll.interval.ms")).isEqualTo("2000");
        assertThat(config.getString("max.queue.size")).isEqualTo("16384");
        assertThat(config.getString("max.batch.size")).isEqualTo("4096");
    }

    @Test
    @DisplayName("MONGODB configurer maps all mongo keys")
    void mongodb_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.MONGODB);
        properties.setServerName("mongoserver");

        DebeziumConnectorProperties.MongoDb mongo = properties.getMongoDb();
        mongo.setConnectionString("mongodb://localhost:27017");
        mongo.setAuthSource("admin");
        mongo.setDatabaseList("db1");
        mongo.setCollectionList("db1.col1");
        mongo.setSnapshotMode("initial");
        mongo.setConnectTimeoutMs(30000);
        mongo.setSocketTimeoutMs(30000);
        mongo.setServerSelectionTimeoutMs(30000);
        mongo.setMaxConnectionPoolSize(100);
        mongo.setMinConnectionPoolSize(5);
        mongo.setMaxConnectionIdleTimeMs(30000);
        mongo.setMaxConnectionLifeTimeMs(300000);
        mongo.setTombstonesOnDelete(true);
        mongo.setIncludeQuery(true);
        mongo.setFieldRenames("a:b");
        mongo.setFieldExcludeList("c");
        mongo.setPollIntervalMs(2000);
        mongo.setMaxQueueSize(16384);
        mongo.setMaxBatchSize(4096);
        mongo.setMaxQueueSizeInBytes(2147483648L);

        Configuration config = apply(new MongoDbConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.mongodb.MongoDbConnector");
        assertThat(config.getString("database.server.name")).isEqualTo("mongoserver");
        assertThat(config.getString("mongodb.connection.string")).isEqualTo("mongodb://localhost:27017");
        assertThat(config.getString("mongodb.authsource")).isEqualTo("admin");
        assertThat(config.getString("database.include.list")).isEqualTo("db1");
        assertThat(config.getString("collection.include.list")).isEqualTo("db1.col1");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("mongodb.connect.timeout.ms")).isEqualTo("30000");
        assertThat(config.getString("mongodb.socket.timeout.ms")).isEqualTo("30000");
        assertThat(config.getString("mongodb.server.selection.timeout.ms")).isEqualTo("30000");
        assertThat(config.getString("mongodb.max.connection.pool.size")).isEqualTo("100");
        assertThat(config.getString("mongodb.min.connection.pool.size")).isEqualTo("5");
        assertThat(config.getString("mongodb.max.connection.idle.time.ms")).isEqualTo("30000");
        assertThat(config.getString("mongodb.max.connection.life.time.ms")).isEqualTo("300000");
        assertThat(config.getString("tombstones.on.delete")).isEqualTo("true");
        assertThat(config.getString("include.query")).isEqualTo("true");
        assertThat(config.getString("field.renames")).isEqualTo("a:b");
        assertThat(config.getString("field.exclude.list")).isEqualTo("c");
        assertThat(config.getString("poll.interval.ms")).isEqualTo("2000");
        assertThat(config.getString("max.queue.size")).isEqualTo("16384");
        assertThat(config.getString("max.batch.size")).isEqualTo("4096");
        assertThat(config.getString("max.queue.size.in.bytes")).isEqualTo("2147483648");
    }

    @Test
    @DisplayName("MONGODB configurer uses host:port when no connection string")
    void mongodb_usesHostPort() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.MONGODB);
        properties.setHost("mongo-host");
        properties.setPort(27017);
        properties.setUsername("user");
        properties.setPassword("pass");

        Configuration config = apply(new MongoDbConnectorConfigurer(), properties);

        assertThat(config.getString("mongodb.hosts")).isEqualTo("mongo-host:27017");
        assertThat(config.getString("mongodb.user")).isEqualTo("user");
        assertThat(config.getString("mongodb.password")).isEqualTo("pass");
    }

    @Test
    @DisplayName("ORACLE configurer maps all oracle keys")
    void oracle_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.ORACLE);
        properties.setHost("oracle-host");
        properties.setPort(1521);
        properties.setUsername("ora");
        properties.setPassword("orapass");
        properties.setServerName("oraserver");
        properties.setDatabaseIncludeList("ORCL");
        properties.setTableIncludeList("ORCL.t1");
        properties.setSchemaIncludeList("HR");

        DebeziumConnectorProperties.Oracle oracle = properties.getOracle();
        oracle.setDatabase("ORCL");
        oracle.setPdbName("PDB1");
        oracle.setSnapshotMode("initial");
        oracle.setLogMiningStrategy("online_catalog");

        Configuration config = apply(new OracleConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.oracle.OracleConnector");
        assertThat(config.getString("database.hostname")).isEqualTo("oracle-host");
        assertThat(config.getString("database.port")).isEqualTo("1521");
        assertThat(config.getString("database.user")).isEqualTo("ora");
        assertThat(config.getString("database.password")).isEqualTo("orapass");
        assertThat(config.getString("database.server.name")).isEqualTo("oraserver");
        assertThat(config.getString("database.dbname")).isEqualTo("ORCL");
        assertThat(config.getString("database.pdb.name")).isEqualTo("PDB1");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("log.mining.strategy")).isEqualTo("online_catalog");
        assertThat(config.getString("database.connection.adapter")).isEqualTo("logminer");
    }

    @Test
    @DisplayName("SQLSERVER configurer maps all sqlserver keys")
    void sqlserver_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.SQLSERVER);
        properties.setHost("sqlserver-host");
        properties.setPort(1433);
        properties.setUsername("sa");
        properties.setPassword("sapass");
        properties.setServerName("sqlserver");
        properties.setDatabaseIncludeList("testdb");
        properties.setTableIncludeList("testdb.dbo.t1");
        properties.setSchemaIncludeList("dbo");

        DebeziumConnectorProperties.SqlServer sqlServer = properties.getSqlServer();
        sqlServer.setDatabase("testdb");
        sqlServer.setSnapshotMode("initial");
        sqlServer.setSnapshotIsolationMode("snapshot");

        Configuration config = apply(new SqlServerConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.sqlserver.SqlServerConnector");
        assertThat(config.getString("database.hostname")).isEqualTo("sqlserver-host");
        assertThat(config.getString("database.port")).isEqualTo("1433");
        assertThat(config.getString("database.user")).isEqualTo("sa");
        assertThat(config.getString("database.password")).isEqualTo("sapass");
        assertThat(config.getString("database.server.name")).isEqualTo("sqlserver");
        assertThat(config.getString("database.dbname")).isEqualTo("testdb");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("snapshot.isolation.mode")).isEqualTo("snapshot");
    }

    @Test
    @DisplayName("DB2 configurer maps all db2 keys")
    void db2_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.DB2);
        properties.setHost("db2-host");
        properties.setPort(50000);
        properties.setUsername("db2inst1");
        properties.setPassword("db2pass");
        properties.setServerName("db2server");
        properties.setDatabaseName("TESTDB");
        properties.setDatabaseIncludeList("TESTDB");
        properties.setTableIncludeList("TESTDB.SAMPLE.t1");
        properties.setSchemaIncludeList("SAMPLE");

        DebeziumConnectorProperties.PostgreSql pg = properties.getPostgreSql();
        pg.setSnapshotMode("initial");

        Configuration config = apply(new Db2ConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.db2.Db2Connector");
        assertThat(config.getString("database.hostname")).isEqualTo("db2-host");
        assertThat(config.getString("database.port")).isEqualTo("50000");
        assertThat(config.getString("database.user")).isEqualTo("db2inst1");
        assertThat(config.getString("database.password")).isEqualTo("db2pass");
    }

    @Test
    @DisplayName("CASSANDRA configurer maps all cassandra keys")
    void cassandra_mapsAllKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.CASSANDRA);
        properties.setServerName("cassandrserver");
        properties.setDatabaseIncludeList("ks1");
        properties.setTableIncludeList("ks1.t1");

        DebeziumConnectorProperties.Cassandra cassandra = properties.getCassandra();
        cassandra.setConnectionString("localhost:9042");
        cassandra.setDatabaseList("ks1");
        cassandra.setTableList("ks1.t1");
        cassandra.setSnapshotMode("initial");
        cassandra.setConnectTimeoutMs(30000);
        cassandra.setReadTimeoutMs(30000);
        cassandra.setTombstonesOnDelete(true);
        cassandra.setIncludeQuery(true);
        cassandra.setPollIntervalMs(2000);
        cassandra.setMaxQueueSize(16384);
        cassandra.setMaxBatchSize(4096);

        Configuration config = apply(new CassandraConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.cassandra.CassandraConnector");
        assertThat(config.getString("database.server.name")).isEqualTo("cassandrserver");
        assertThat(config.getString("keyspace.include.list")).isEqualTo("ks1");
        assertThat(config.getString("table.include.list")).isEqualTo("ks1.t1");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("cassandra.connect.timeout.ms")).isEqualTo("30000");
        assertThat(config.getString("cassandra.read.timeout.ms")).isEqualTo("30000");
        assertThat(config.getString("tombstones.on.delete")).isEqualTo("true");
        assertThat(config.getString("include.query")).isEqualTo("true");
        assertThat(config.getString("poll.interval.ms")).isEqualTo("2000");
        assertThat(config.getString("max.queue.size")).isEqualTo("16384");
        assertThat(config.getString("max.batch.size")).isEqualTo("4096");
    }

    @Test
    @DisplayName("VITESS configurer maps vitess keys")
    void vitess_mapsKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.VITESS);
        properties.setHost("vitess-host");
        properties.setPort(15991);
        properties.setUsername("vt");
        properties.setPassword("vtpass");
        properties.setServerName("vitessserver");
        properties.setDatabaseIncludeList("commerce");
        properties.setTableIncludeList("commerce.orders");

        Configuration config = apply(new VitessConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.vitess.VitessConnector");
        assertThat(config.getString("database.server.name")).isEqualTo("vitessserver");
        assertThat(config.getString("vitess.hosts")).isEqualTo("vitess-host:15991");
        assertThat(config.getString("vitess.user")).isEqualTo("vt");
        assertThat(config.getString("vitess.password")).isEqualTo("vtpass");
        assertThat(config.getString("keyspace.include.list")).isEqualTo("commerce");
        assertThat(config.getString("table.include.list")).isEqualTo("commerce.orders");
    }

    @Test
    @DisplayName("SPANNER configurer maps spanner keys")
    void spanner_mapsKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.SPANNER);
        properties.setServerName("spannerserver");
        properties.setDatabaseIncludeList("db1");
        properties.setTableIncludeList("db1.t1");

        DebeziumConnectorProperties.Spanner spanner = properties.getSpanner();
        spanner.setProjectId("my-project");
        spanner.setInstanceId("my-instance");
        spanner.setDatabaseId("my-db");
        spanner.setConnectionString("spanner://localhost");
        spanner.setDatabaseList("db1");
        spanner.setTableList("db1.t1");
        spanner.setSnapshotMode("initial");
        spanner.setTombstonesOnDelete(true);
        spanner.setIncludeQuery(true);
        spanner.setPollIntervalMs(2000);
        spanner.setMaxQueueSize(16384);
        spanner.setMaxBatchSize(4096);

        Configuration config = apply(new SpannerConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.spanner.SpannerConnector");
        assertThat(config.getString("database.server.name")).isEqualTo("spannerserver");
        assertThat(config.getString("database.include.list")).isEqualTo("db1");
        assertThat(config.getString("table.include.list")).isEqualTo("db1.t1");
        assertThat(config.getString("snapshot.mode")).isEqualTo("initial");
        assertThat(config.getString("tombstones.on.delete")).isEqualTo("true");
        assertThat(config.getString("include.query")).isEqualTo("true");
        assertThat(config.getString("poll.interval.ms")).isEqualTo("2000");
        assertThat(config.getString("max.queue.size")).isEqualTo("16384");
        assertThat(config.getString("max.batch.size")).isEqualTo("4096");
    }

    @Test
    @DisplayName("INFORMIX configurer maps informix keys")
    void informix_mapsKeys() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.INFORMIX);
        properties.setHost("informix-host");
        properties.setPort(9088);
        properties.setUsername("informix");
        properties.setPassword("ifxpass");
        properties.setServerName("ifxserver");
        properties.setDatabaseIncludeList("testdb");
        properties.setTableIncludeList("testdb.t1");
        properties.setSchemaIncludeList("informix");

        Configuration config = apply(new InformixConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("io.debezium.connector.informix.InformixConnector");
        assertThat(config.getString("database.hostname")).isEqualTo("informix-host");
        assertThat(config.getString("database.port")).isEqualTo("9088");
        assertThat(config.getString("database.user")).isEqualTo("informix");
        assertThat(config.getString("database.password")).isEqualTo("ifxpass");
        assertThat(config.getString("database.server.name")).isEqualTo("ifxserver");
        assertThat(config.getString("database.include.list")).isEqualTo("testdb");
        assertThat(config.getString("table.include.list")).isEqualTo("testdb.t1");
        assertThat(config.getString("schema.include.list")).isEqualTo("informix");
    }

    @Test
    @DisplayName("CUSTOM configurer forwards class name and props")
    void custom_forwardsProps() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.setType(ConnectorType.CUSTOM);
        Map<String, String> props = new HashMap<>();
        props.put("my.key", "my.value");
        properties.getCustom().setConnectorClass("com.example.MyConnector");
        properties.getCustom().setProps(props);

        Configuration config = apply(new CustomConnectorConfigurer(), properties);

        assertThat(config.getString("connector.class")).isEqualTo("com.example.MyConnector");
        assertThat(config.getString("my.key")).isEqualTo("my.value");
    }

    @Test
    @DisplayName("CUSTOM configurer does nothing without connector class")
    void custom_withoutClassName_isNoOp() {
        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        properties.getCustom().setConnectorClass(null);

        Configuration config = apply(new CustomConnectorConfigurer(), properties);

        assertThat(config.hasKey("connector.class")).isFalse();
    }

    private Configuration apply(ConnectorConfigurer configurer, DebeziumConnectorProperties properties) {
        Configuration.Builder builder = Configuration.create();
        configurer.apply(builder, properties);
        return builder.build();
    }
}

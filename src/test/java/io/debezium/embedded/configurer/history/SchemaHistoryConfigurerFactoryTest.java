package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SchemaHistoryConfigurerFactory} and every
 * {@link SchemaHistoryConfigurer} implementation.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("SchemaHistory configurers")
class SchemaHistoryConfigurerFactoryTest {

    @Test
    @DisplayName("factory resolves every supported type")
    void from_resolvesAllTypes() {
        for (SchemaHistoryType type : SchemaHistoryType.values()) {
            DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
            properties.setType(type);
            SchemaHistoryConfigurer configurer = SchemaHistoryConfigurerFactory.from(properties);
            assertThat(configurer).as("configurer for %s", type).isNotNull();
        }
    }

    @Test
    @DisplayName("FILE configurer sets FileSchemaHistory class and filename")
    void file_setsFileSchemaHistory() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.FILE);
        properties.getFile().setFilename("myhistory.dat");

        Configuration config = apply(new FileSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.file.history.FileSchemaHistory");
        assertThat(config.getString("schema.history.internal.file")).isEqualTo("myhistory.dat");
    }

    @Test
    @DisplayName("FILE configurer skips blank filename")
    void file_skipsBlankFilename() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.getFile().setFilename("  ");

        Configuration config = apply(new FileSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.file.history.FileSchemaHistory");
        assertThat(config.hasKey("schema.history.internal.file")).isFalse();
    }

    @Test
    @DisplayName("KAFKA configurer maps all kafka keys")
    void kafka_mapsAllKeys() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.KAFKA);
        DebeziumSchemaHistoryProperties.Kafka kafka = properties.getKafka();
        kafka.setTopic("schema-topic");
        kafka.setBootstrapServers("localhost:9092");
        kafka.setRecoveryAttempts(50);
        kafka.setRecoveryPollIntervalMs(200);
        kafka.setQueryTimeoutMs(10);
        kafka.setCreateTimeoutMs(60);

        // Producer
        kafka.getProducer().setAcks("1");
        kafka.getProducer().setRetries(5);
        kafka.getProducer().setBatchSize(32768);
        kafka.getProducer().setLingerMs(5);
        kafka.getProducer().setBufferMemory(67108864);
        kafka.getProducer().setCompressionType("snappy");
        kafka.getProducer().setMaxRequestSize(2097152);
        kafka.getProducer().setRequestTimeoutMs(60000);
        kafka.getProducer().setMetadataMaxAgeMs(600000);
        kafka.getProducer().setConnectionsMaxIdleMs(600000);
        kafka.getProducer().setReconnectBackoffMs(100);
        kafka.getProducer().setRetryBackoffMs(200);

        // Consumer
        kafka.getConsumer().setAutoOffsetReset("latest");
        kafka.getConsumer().setEnableAutoCommit(true);
        kafka.getConsumer().setSessionTimeoutMs(60000);
        kafka.getConsumer().setHeartbeatIntervalMs(5000);
        kafka.getConsumer().setMaxPollRecords(1000);
        kafka.getConsumer().setMaxPollIntervalMs(600000);
        kafka.getConsumer().setRequestTimeoutMs(60000);
        kafka.getConsumer().setFetchMinBytes(5);
        kafka.getConsumer().setFetchMaxWaitMs(1000);
        kafka.getConsumer().setConnectionsMaxIdleMs(600000);
        kafka.getConsumer().setReconnectBackoffMs(100);
        kafka.getConsumer().setRetryBackoffMs(200);

        // Security
        kafka.getSecurity().setSecurityProtocol("SASL_SSL");
        kafka.getSecurity().setSaslMechanism("PLAIN");
        kafka.getSecurity().setSaslUsername("user");
        kafka.getSecurity().setSaslPassword("pass");
        kafka.getSecurity().setSslTruststoreLocation("/ts.jks");
        kafka.getSecurity().setSslTruststorePassword("tspass");
        kafka.getSecurity().setSslKeystoreLocation("/ks.jks");
        kafka.getSecurity().setSslKeystorePassword("kspass");
        kafka.getSecurity().setSslKeyPassword("keypass");
        kafka.getSecurity().setSslEndpointIdentificationAlgorithm("https");

        Configuration config = apply(new KafkaSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.kafka.history.KafkaSchemaHistory");
        assertThat(config.getString("schema.history.internal.kafka.topic")).isEqualTo("schema-topic");
        assertThat(config.getString("schema.history.internal.kafka.bootstrap.servers")).isEqualTo("localhost:9092");
        assertThat(config.getString("schema.history.internal.kafka.recovery.attempts")).isEqualTo("50");
        assertThat(config.getString("schema.history.internal.kafka.recovery.poll.interval.ms")).isEqualTo("200");
        assertThat(config.getString("schema.history.internal.kafka.query.timeout.ms")).isEqualTo("10");
        assertThat(config.getString("schema.history.internal.kafka.create.timeout.ms")).isEqualTo("60");
        // Producer
        assertThat(config.getString("schema.history.internal.kafka.producer.acks")).isEqualTo("1");
        assertThat(config.getString("schema.history.internal.kafka.producer.retries")).isEqualTo("5");
        assertThat(config.getString("schema.history.internal.kafka.producer.batch.size")).isEqualTo("32768");
        assertThat(config.getString("schema.history.internal.kafka.producer.linger.ms")).isEqualTo("5");
        assertThat(config.getString("schema.history.internal.kafka.producer.buffer.memory")).isEqualTo("67108864");
        assertThat(config.getString("schema.history.internal.kafka.producer.compression.type")).isEqualTo("snappy");
        assertThat(config.getString("schema.history.internal.kafka.producer.max.request.size")).isEqualTo("2097152");
        assertThat(config.getString("schema.history.internal.kafka.producer.request.timeout.ms")).isEqualTo("60000");
        assertThat(config.getString("schema.history.internal.kafka.producer.metadata.max.age.ms")).isEqualTo("600000");
        assertThat(config.getString("schema.history.internal.kafka.producer.connections.max.idle.ms")).isEqualTo("600000");
        assertThat(config.getString("schema.history.internal.kafka.producer.reconnect.backoff.ms")).isEqualTo("100");
        assertThat(config.getString("schema.history.internal.kafka.producer.retry.backoff.ms")).isEqualTo("200");
        // Consumer
        assertThat(config.getString("schema.history.internal.kafka.consumer.auto.offset.reset")).isEqualTo("latest");
        assertThat(config.getString("schema.history.internal.kafka.consumer.enable.auto.commit")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.kafka.consumer.session.timeout.ms")).isEqualTo("60000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.heartbeat.interval.ms")).isEqualTo("5000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.max.poll.records")).isEqualTo("1000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.max.poll.interval.ms")).isEqualTo("600000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.request.timeout.ms")).isEqualTo("60000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.fetch.min.bytes")).isEqualTo("5");
        assertThat(config.getString("schema.history.internal.kafka.consumer.fetch.max.wait.ms")).isEqualTo("1000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.connections.max.idle.ms")).isEqualTo("600000");
        assertThat(config.getString("schema.history.internal.kafka.consumer.reconnect.backoff.ms")).isEqualTo("100");
        assertThat(config.getString("schema.history.internal.kafka.consumer.retry.backoff.ms")).isEqualTo("200");
        // Security
        assertThat(config.getString("schema.history.internal.kafka.security.protocol")).isEqualTo("SASL_SSL");
        assertThat(config.getString("schema.history.internal.kafka.sasl.mechanism")).isEqualTo("PLAIN");
        assertThat(config.getString("schema.history.internal.kafka.sasl.username")).isEqualTo("user");
        assertThat(config.getString("schema.history.internal.kafka.sasl.password")).isEqualTo("pass");
        assertThat(config.getString("schema.history.internal.kafka.ssl.truststore.location")).isEqualTo("/ts.jks");
        assertThat(config.getString("schema.history.internal.kafka.ssl.truststore.password")).isEqualTo("tspass");
        assertThat(config.getString("schema.history.internal.kafka.ssl.keystore.location")).isEqualTo("/ks.jks");
        assertThat(config.getString("schema.history.internal.kafka.ssl.keystore.password")).isEqualTo("kspass");
        assertThat(config.getString("schema.history.internal.kafka.ssl.key.password")).isEqualTo("keypass");
        assertThat(config.getString("schema.history.internal.kafka.ssl.endpoint.identification.algorithm")).isEqualTo("https");
    }

    @Test
    @DisplayName("JDBC configurer maps all jdbc keys")
    void jdbc_mapsAllKeys() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.JDBC);
        DebeziumSchemaHistoryProperties.Jdbc jdbc = properties.getJdbc();
        jdbc.setUrl("jdbc:postgresql://localhost/test");
        jdbc.setUsername("sa");
        jdbc.setPassword("secret");
        jdbc.setTableName("db_history");
        jdbc.setPoolSize(5);
        jdbc.setMinConnections(2);
        jdbc.setMaxConnections(50);
        jdbc.setMaxConnectionLifetime(3600000);
        jdbc.setMaxConnectionIdleTime(300000);
        jdbc.setConnectionTimeout(15000);
        jdbc.setQueryTimeout(15000);
        jdbc.setConnectionValidationTimeout(3000);
        jdbc.setConnectionValidationQuery("SELECT 1");
        jdbc.setLeakDetectionThreshold(true);
        jdbc.setLeakDetectionThresholdMs(30000);
        jdbc.setAutoCommit(false);
        jdbc.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
        jdbc.setUseSSL(true);
        jdbc.setSslMode("REQUIRED");
        jdbc.setVerifyServerCertificate(false);
        jdbc.setAllowPublicKeyRetrieval(true);
        jdbc.setCharacterEncoding("UTF-8");
        jdbc.setTimezone("Asia/Shanghai");
        jdbc.setMaxRetries(5);
        jdbc.setRetryDelayMs(2000);
        jdbc.setTableDdl("CREATE TABLE ...");
        jdbc.setTableSelect("SELECT * FROM %s");
        jdbc.setTableExistSelect("SELECT 1 FROM %s");
        jdbc.setTableInsert("INSERT INTO %s ...");

        Configuration config = apply(new JdbcSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.jdbc.history.JdbcSchemaHistory");
        assertThat(config.getString("schema.history.internal.jdbc.connection.url")).isEqualTo("jdbc:postgresql://localhost/test");
        assertThat(config.getString("schema.history.internal.jdbc.connection.user")).isEqualTo("sa");
        assertThat(config.getString("schema.history.internal.jdbc.connection.password")).isEqualTo("secret");
        assertThat(config.getString("schema.history.internal.jdbc.table.name")).isEqualTo("db_history");
        assertThat(config.getString("schema.history.internal.jdbc.connection.pool.size")).isEqualTo("5");
        assertThat(config.getString("schema.history.internal.jdbc.connection.pool.min.connections")).isEqualTo("2");
        assertThat(config.getString("schema.history.internal.jdbc.connection.pool.max.connections")).isEqualTo("50");
        assertThat(config.getString("schema.history.internal.jdbc.connection.pool.max.lifetime.ms")).isEqualTo("3600000");
        assertThat(config.getString("schema.history.internal.jdbc.connection.pool.max.idle.time.ms")).isEqualTo("300000");
        assertThat(config.getString("schema.history.internal.jdbc.connection.timeout.ms")).isEqualTo("15000");
        assertThat(config.getString("schema.history.internal.jdbc.query.timeout.ms")).isEqualTo("15000");
        assertThat(config.getString("schema.history.internal.jdbc.connection.validation.timeout.ms")).isEqualTo("3000");
        assertThat(config.getString("schema.history.internal.jdbc.connection.validation.query")).isEqualTo("SELECT 1");
        assertThat(config.getString("schema.history.internal.jdbc.connection.leak.detection.threshold")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.jdbc.connection.leak.detection.threshold.ms")).isEqualTo("30000");
        assertThat(config.getString("schema.history.internal.jdbc.auto.commit")).isEqualTo("false");
        assertThat(config.getString("schema.history.internal.jdbc.transaction.isolation")).isEqualTo("TRANSACTION_REPEATABLE_READ");
        assertThat(config.getString("schema.history.internal.jdbc.use.ssl")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.jdbc.ssl.mode")).isEqualTo("REQUIRED");
        assertThat(config.getString("schema.history.internal.jdbc.verify.server.certificate")).isEqualTo("false");
        assertThat(config.getString("schema.history.internal.jdbc.allow.public.key.retrieval")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.jdbc.character.encoding")).isEqualTo("UTF-8");
        assertThat(config.getString("schema.history.internal.jdbc.timezone")).isEqualTo("Asia/Shanghai");
        assertThat(config.getString("schema.history.internal.jdbc.connection.retry.max.attempts")).isEqualTo("5");
        assertThat(config.getString("schema.history.internal.jdbc.connection.retry.delay.ms")).isEqualTo("2000");
        assertThat(config.getString("schema.history.internal.jdbc.table.ddl")).isEqualTo("CREATE TABLE ...");
        assertThat(config.getString("schema.history.internal.jdbc.table.select")).isEqualTo("SELECT * FROM %s");
        assertThat(config.getString("schema.history.internal.jdbc.table.exists.select")).isEqualTo("SELECT 1 FROM %s");
        assertThat(config.getString("schema.history.internal.jdbc.table.insert")).isEqualTo("INSERT INTO %s ...");
    }

    @Test
    @DisplayName("REDIS configurer maps all redis keys")
    void redis_mapsAllKeys() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.REDIS);
        DebeziumSchemaHistoryProperties.Redis redis = properties.getRedis();
        redis.setKey("mykey");
        redis.setAddress("localhost:6379");
        redis.setUser("ruser");
        redis.setPassword("rpass");
        redis.setDbIndex(3);
        redis.setSslEnabled(true);
        redis.setSslHostnameVerificationEnabled(true);
        redis.setSslTruststorePath("/ts.p12");
        redis.setSslTruststorePassword("tspass");
        redis.setSslTruststoreType("PKCS12");
        redis.setSslKeystorePath("/ks.p12");
        redis.setSslKeystorePassword("kspass");
        redis.setSslKeystoreType("PKCS12");
        redis.setConnectionTimeoutMs(3000);
        redis.setSocketTimeoutMs(3000);
        redis.setRetryInitialDelayMs(500);
        redis.setRetryMaxDelayMs(15000);
        redis.setRetryMaxAttempts(5);
        redis.setWaitEnabled(true);
        redis.setWaitTimeoutMs(2000);
        redis.setWaitRetryEnabled(true);
        redis.setWaitRetryDelayMs(200);

        Configuration config = apply(new RedisSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.redis.history.RedisSchemaHistory");
        assertThat(config.getString("schema.history.internal.redis.key")).isEqualTo("mykey");
        assertThat(config.getString("schema.history.internal.redis.address")).isEqualTo("localhost:6379");
        assertThat(config.getString("schema.history.internal.redis.user")).isEqualTo("ruser");
        assertThat(config.getString("schema.history.internal.redis.password")).isEqualTo("rpass");
        assertThat(config.getString("schema.history.internal.redis.db.index")).isEqualTo("3");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.enabled")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.hostname.verification.enabled")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.truststore.path")).isEqualTo("/ts.p12");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.truststore.password")).isEqualTo("tspass");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.truststore.type")).isEqualTo("PKCS12");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.keystore.path")).isEqualTo("/ks.p12");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.keystore.password")).isEqualTo("kspass");
        assertThat(config.getString("schema.history.internal.storage.redis.ssl.keystore.type")).isEqualTo("PKCS12");
        assertThat(config.getString("schema.history.internal.storage.redis.connection.timeout.ms")).isEqualTo("3000");
        assertThat(config.getString("schema.history.internal.storage.redis.socket.timeout.ms")).isEqualTo("3000");
        assertThat(config.getString("schema.history.internal.storage.redis.retry.initial.delay.ms")).isEqualTo("500");
        assertThat(config.getString("schema.history.internal.storage.redis.retry.max.delay.ms")).isEqualTo("15000");
        assertThat(config.getString("schema.history.internal.storage.redis.retry.max.attempts")).isEqualTo("5");
        assertThat(config.getString("schema.history.internal.storage.redis.wait.enabled")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.storage.redis.wait.timeout.ms")).isEqualTo("2000");
        assertThat(config.getString("schema.history.internal.storage.redis.wait.retry.enabled")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.storage.redis.wait.retry.delay.ms")).isEqualTo("200");
    }

    @Test
    @DisplayName("S3 configurer maps all s3 keys")
    void s3_mapsAllKeys() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.S3);
        DebeziumSchemaHistoryProperties.S3 s3 = properties.getS3();
        s3.setBucketName("my-bucket");
        s3.setObjectName("history/obj");
        s3.setRegionName("us-east-1");
        s3.setAccessKeyId("AKID");
        s3.setSecretAccessKey("SECRET");
        s3.setEndpointUrl("https://s3.example.com");

        Configuration config = apply(new AmazonS3SchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.s3.history.S3SchemaHistory");
        assertThat(config.getString("schema.history.internal.s3.bucket.name")).isEqualTo("my-bucket");
        assertThat(config.getString("schema.history.internal.s3.object.name")).isEqualTo("history/obj");
        assertThat(config.getString("schema.history.internal.s3.region.name")).isEqualTo("us-east-1");
        assertThat(config.getString("schema.history.internal.s3.access.key.id")).isEqualTo("AKID");
        assertThat(config.getString("schema.history.internal.s3.secret.access.key")).isEqualTo("SECRET");
        assertThat(config.getString("schema.history.internal.s3.endpoint")).isEqualTo("https://s3.example.com");
    }

    @Test
    @DisplayName("ROCKETMQ configurer maps all rocketmq keys")
    void rocketmq_mapsAllKeys() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.ROCKETMQ);
        DebeziumSchemaHistoryProperties.RocketMq rocketMq = properties.getRocketMq();
        rocketMq.setTopic("schema-topic");
        rocketMq.setNameSrvAddr("localhost:9876");
        rocketMq.setAclEnabled(true);
        rocketMq.setAccessKey("ak");
        rocketMq.setSecretKey("sk");
        rocketMq.setRecoveryAttempts(10);
        rocketMq.setRecoveryPollIntervalMs(500);
        rocketMq.setStoreRecordTimeoutMs(5000);

        Configuration config = apply(new RocketMqSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.rocketmq.history.RocketMqSchemaHistory");
        assertThat(config.getString("schema.history.internal.rocketmq.topic")).isEqualTo("schema-topic");
        assertThat(config.getString("schema.history.internal.rocketmq.name.srv.addr")).isEqualTo("localhost:9876");
        assertThat(config.getString("schema.history.internal.rocketmq.acl.enabled")).isEqualTo("true");
        assertThat(config.getString("schema.history.internal.rocketmq.access.key")).isEqualTo("ak");
        assertThat(config.getString("schema.history.internal.rocketmq.secret.key")).isEqualTo("sk");
        assertThat(config.getString("schema.history.internal.rocketmq.recovery.attempts")).isEqualTo("10");
        assertThat(config.getString("schema.history.internal.rocketmq.recovery.poll.interval.ms")).isEqualTo("500");
        assertThat(config.getString("schema.history.internal.rocketmq.store.record.timeout.ms")).isEqualTo("5000");
    }

    @Test
    @DisplayName("AZURE_BLOB configurer maps all azure keys")
    void azureBlob_mapsAllKeys() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.AZURE_BLOB);
        DebeziumSchemaHistoryProperties.AzureBlob azureBlob = properties.getAzureBlob();
        azureBlob.setConnectionString("DefaultEndpointsProtocol=https;...");
        azureBlob.setAccountName("myaccount");
        azureBlob.setContainerName("mycontainer");
        azureBlob.setBlobName("myblob");

        Configuration config = apply(new AzureBlobSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.storage.azure.blob.history.AzureBlobSchemaHistory");
        assertThat(config.getString("schema.history.internal.azure.storage.account.connectionstring")).isEqualTo("DefaultEndpointsProtocol=https;...");
        assertThat(config.getString("schema.history.internal.azure.storage.account.name")).isEqualTo("myaccount");
        assertThat(config.getString("schema.history.internal.azure.storage.account.container.name")).isEqualTo("mycontainer");
        assertThat(config.getString("schema.history.internal.azure.storage.blob.name")).isEqualTo("myblob");
    }

    @Test
    @DisplayName("MEMORY configurer sets MemorySchemaHistory")
    void memory_setsMemorySchemaHistory() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        Configuration config = apply(new MemorySchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal"))
            .isEqualTo("io.debezium.relational.history.MemorySchemaHistory");
    }

    @Test
    @DisplayName("CUSTOM configurer forwards class name and prefixed props")
    void custom_forwardsClassNameAndProps() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.setType(SchemaHistoryType.CUSTOM);
        properties.getCustom().setHistoryClass("com.example.MyHistory");
        Map<String, String> props = new HashMap<>();
        props.put("schema.history.internal.foo", "bar");
        props.put("extra.key", "extraValue");
        properties.getCustom().setProps(props);

        Configuration config = apply(new CustomSchemaHistoryConfigurer(), properties);

        assertThat(config.getString("schema.history.internal")).isEqualTo("com.example.MyHistory");
        assertThat(config.getString("schema.history.internal.foo")).isEqualTo("bar");
        assertThat(config.getString("schema.history.internal.extra.key")).isEqualTo("extraValue");
    }

    @Test
    @DisplayName("CUSTOM configurer does nothing without a class name")
    void custom_withoutClassName_isNoOp() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        properties.getCustom().setHistoryClass(null);

        Configuration config = apply(new CustomSchemaHistoryConfigurer(), properties);

        assertThat(config.hasKey("schema.history.internal")).isFalse();
    }

    private Configuration apply(SchemaHistoryConfigurer configurer, DebeziumSchemaHistoryProperties properties) {
        Configuration.Builder builder = Configuration.create();
        configurer.apply(builder, properties);
        return builder.build();
    }
}

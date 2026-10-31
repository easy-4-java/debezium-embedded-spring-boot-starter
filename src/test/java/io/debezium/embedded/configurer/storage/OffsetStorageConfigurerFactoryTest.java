package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OffsetStorageConfigurerFactory} and every
 * {@link OffsetStorageConfigurer} implementation.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("OffsetStorage configurers")
class OffsetStorageConfigurerFactoryTest {

    @Test
    @DisplayName("factory resolves every supported type")
    void from_resolvesAllTypes() {
        for (OffsetStorageType type : OffsetStorageType.values()) {
            DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
            properties.setType(type);
            OffsetStorageConfigurer configurer = OffsetStorageConfigurerFactory.from(properties);
            assertThat(configurer).as("configurer for %s", type).isNotNull();
        }
    }

    @Test
    @DisplayName("MEMORY configurer sets MemoryOffsetBackingStore")
    void memory_setsMemoryBackingStore() {
        Configuration config = apply(new MemoryOffsetStorageConfigurer(), new DebeziumOffsetStorageProperties());
        assertThat(config.getString("offset.storage"))
            .isEqualTo("org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
    }

    @Test
    @DisplayName("FILE configurer maps all file keys")
    void file_mapsAllKeys() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        properties.getFile().setFileName("/tmp/offsets.dat");
        properties.getFile().setFlushIntervalMs(10000);
        properties.getFile().setFlushTimeoutMs(5000);

        Configuration config = apply(new FileOffsetStorageConfigurer(), properties);

        assertThat(config.getString("offset.storage"))
            .isEqualTo("org.apache.kafka.connect.storage.FileOffsetBackingStore");
        assertThat(config.getString("offset.storage.file.filename")).isEqualTo("/tmp/offsets.dat");
        assertThat(config.getString("offset.flush.interval.ms")).isEqualTo("10000");
        assertThat(config.getString("offset.flush.timeout.ms")).isEqualTo("5000");
    }

    @Test
    @DisplayName("FILE configurer skips blank filename")
    void file_skipsBlankFileName() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        properties.getFile().setFileName("  ");

        Configuration config = apply(new FileOffsetStorageConfigurer(), properties);

        assertThat(config.hasKey("offset.storage.file.filename")).isFalse();
    }

    @Test
    @DisplayName("KAFKA configurer maps all kafka keys")
    void kafka_mapsAllKeys() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        properties.getKafka().setTopic("offset-topic");
        properties.getKafka().setPartitions(25);
        properties.getKafka().setReplicationFactor(3);

        Configuration config = apply(new KafkaOffsetStorageConfigurer(), properties);

        assertThat(config.getString("offset.storage"))
            .isEqualTo("org.apache.kafka.connect.storage.KafkaOffsetBackingStore");
        assertThat(config.getString("offset.storage.topic")).isEqualTo("offset-topic");
        assertThat(config.getString("offset.storage.partitions")).isEqualTo("25");
        assertThat(config.getString("offset.storage.replication.factor")).isEqualTo("3");
    }

    @Test
    @DisplayName("JDBC configurer maps all jdbc keys")
    void jdbc_mapsAllKeys() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        DebeziumOffsetStorageProperties.Jdbc jdbc = properties.getJdbc();
        jdbc.setOffsetStorageUrl("jdbc:postgresql://db/offsets");
        jdbc.setOffsetStorageUsername("sa");
        jdbc.setOffsetStoragePassword("secret");
        jdbc.setOffsetStorageRetryDelayMs(500);
        jdbc.setOffsetStorageMaxRetries(3);
        jdbc.setOffsetStorageTableName("offsets");
        jdbc.setOffsetStorageTableDdl("create");
        jdbc.setOffsetStorageTableSelect("select");
        jdbc.setOffsetStorageTableInsert("insert");
        jdbc.setOffsetStorageTableDelete("delete");

        Configuration config = apply(new JdbcOffsetStorageConfigurer(), properties);

        assertThat(config.getString("offset.storage"))
            .isEqualTo("io.debezium.storage.jdbc.offset.JdbcOffsetBackingStore");
        assertThat(config.getString("offset.storage.jdbc.connection.url")).isEqualTo("jdbc:postgresql://db/offsets");
        assertThat(config.getString("offset.storage.jdbc.connection.user")).isEqualTo("sa");
        assertThat(config.getString("offset.storage.jdbc.connection.password")).isEqualTo("secret");
        assertThat(config.getString("offset.storage.jdbc.connection.wait.retry.delay.ms")).isEqualTo("500");
        assertThat(config.getString("offset.storage.jdbc.connection.retry.max.attempts")).isEqualTo("3");
        assertThat(config.getString("offset.storage.jdbc.table.name")).isEqualTo("offsets");
        assertThat(config.getString("offset.storage.jdbc.table.ddl")).isEqualTo("create");
        assertThat(config.getString("offset.storage.jdbc.table.select")).isEqualTo("select");
        assertThat(config.getString("offset.storage.jdbc.table.insert")).isEqualTo("insert");
        assertThat(config.getString("offset.storage.jdbc.table.delete")).isEqualTo("delete");
    }

    @Test
    @DisplayName("REDIS configurer maps all redis keys")
    void redis_mapsAllKeys() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        DebeziumOffsetStorageProperties.Redis redis = properties.getRedis();
        redis.setKey("redis-key");
        redis.setAddress("localhost:6379");
        redis.setUser("ruser");
        redis.setPassword("rpass");
        redis.setDbIndex(2);
        redis.setSslEnabled(true);
        redis.setSslHostnameVerificationEnabled(false);
        redis.setSslTruststorePath("/ts.p12");
        redis.setSslTruststorePassword("ts");
        redis.setSslTruststoreType("PKCS12");
        redis.setSslKeystorePath("/ks.p12");
        redis.setSslKeystorePassword("ks");
        redis.setSslKeystoreType("PKCS12");
        redis.setConnectionTimeoutMs(2000);
        redis.setSocketTimeoutMs(1500);
        redis.setRetryInitialDelayMs(100);
        redis.setRetryMaxDelayMs(2000);
        redis.setRetryMaxAttempts(3);
        redis.setWaitEnabled(true);
        redis.setWaitTimeoutMs(1000);
        redis.setWaitRetryEnabled(false);
        redis.setWaitRetryDelayMs(50);

        Configuration config = apply(new RedisOffsetStorageConfigurer(), properties);

        assertThat(config.getString("offset.storage"))
            .isEqualTo("io.debezium.storage.redis.offset.RedisOffsetBackingStore");
        assertThat(config.getString("offset.storage.redis.key")).isEqualTo("redis-key");
        assertThat(config.getString("offset.storage.redis.address")).isEqualTo("localhost:6379");
        assertThat(config.getString("offset.storage.redis.user")).isEqualTo("ruser");
        assertThat(config.getString("offset.storage.redis.password")).isEqualTo("rpass");
        assertThat(config.getString("offset.storage.redis.db.index")).isEqualTo("2");
        assertThat(config.getString("offset.storage.redis.ssl.enabled")).isEqualTo("true");
        assertThat(config.getString("offset.storage.redis.ssl.hostname.verification.enabled")).isEqualTo("false");
        assertThat(config.getString("offset.storage.redis.ssl.truststore.path")).isEqualTo("/ts.p12");
        assertThat(config.getString("offset.storage.redis.connection.timeout.ms")).isEqualTo("2000");
        assertThat(config.getString("offset.storage.redis.retry.max.attempts")).isEqualTo("3");
        assertThat(config.getString("offset.storage.redis.wait.enabled")).isEqualTo("true");
        assertThat(config.getString("offset.storage.redis.wait.retry.delay.ms")).isEqualTo("50");
    }

    @Test
    @DisplayName("CUSTOM configurer forwards class name and prefixed props")
    void custom_mapsClassNameAndProps() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        Map<String, String> props = new HashMap<>();
        props.put("offset.storage.foo", "bar");
        props.put("extra", "value");
        properties.getCustom().setClassName("com.example.MyStore");
        properties.getCustom().setProps(props);

        Configuration config = apply(new CustomOffsetStorageConfigurer(), properties);

        assertThat(config.getString("offset.storage")).isEqualTo("com.example.MyStore");
        assertThat(config.getString("offset.storage.foo")).isEqualTo("bar");
        assertThat(config.getString("offset.storage.extra")).isEqualTo("value");
    }

    @Test
    @DisplayName("CUSTOM configurer does nothing without a class name")
    void custom_withoutClassName_isNoOp() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        properties.getCustom().setClassName(null);

        Configuration config = apply(new CustomOffsetStorageConfigurer(), properties);

        assertThat(config.hasKey("offset.storage")).isFalse();
    }

    private Configuration apply(OffsetStorageConfigurer configurer, DebeziumOffsetStorageProperties properties) {
        Configuration.Builder builder = Configuration.create();
        configurer.apply(builder, properties);
        return builder.build();
    }
}

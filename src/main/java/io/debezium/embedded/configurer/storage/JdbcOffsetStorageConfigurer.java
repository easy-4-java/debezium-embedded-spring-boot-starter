package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import io.debezium.embedded.util.PropertyMappers;

/**
 * {@link OffsetStorageConfigurer} for JDBC based offset storage.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class JdbcOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * Applies the storage configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the offset-storage configuration properties
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {

        DebeziumOffsetStorageProperties.Jdbc jdbc = properties.getJdbc();
        PropertyMapper map = PropertyMappers.whenNonNull();

        // Offset Store
        builder.with("offset.storage", "io.debezium.storage.jdbc.offset.JdbcOffsetBackingStore");
        map.from(jdbc::getOffsetStorageUrl).whenHasText().to(value -> builder.with("offset.storage.jdbc.connection.url", value));
        map.from(jdbc::getOffsetStorageUsername).whenHasText().to(value -> builder.with("offset.storage.jdbc.connection.user", value));
        map.from(jdbc::getOffsetStoragePassword).whenHasText().to(value -> builder.with("offset.storage.jdbc.connection.password", value));
        map.from(jdbc::getOffsetStorageRetryDelayMs).whenHasText().to(value -> builder.with("offset.storage.jdbc.connection.wait.retry.delay.ms", value));
        map.from(jdbc::getOffsetStorageMaxRetries).whenHasText().to(value -> builder.with("offset.storage.jdbc.connection.retry.max.attempts", value));
        map.from(jdbc::getOffsetStorageTableName).whenHasText().to(value -> builder.with("offset.storage.jdbc.table.name", value));
        map.from(jdbc::getOffsetStorageTableDdl).whenHasText().to(value -> builder.with("offset.storage.jdbc.table.ddl", value));
        map.from(jdbc::getOffsetStorageTableSelect).whenHasText().to(value -> builder.with("offset.storage.jdbc.table.select", value));
        map.from(jdbc::getOffsetStorageTableInsert).whenHasText().to(value -> builder.with("offset.storage.jdbc.table.insert", value));
        map.from(jdbc::getOffsetStorageTableDelete).whenHasText().to(value -> builder.with("offset.storage.jdbc.table.delete", value));


    }
}

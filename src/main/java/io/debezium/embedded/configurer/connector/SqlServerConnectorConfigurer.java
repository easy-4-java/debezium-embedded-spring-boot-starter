package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import io.debezium.embedded.util.PropertyMappers;
import java.util.Objects;

/**
 * {@link ConnectorConfigurer} for the Debezium Microsoft SQL Server connector.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class SqlServerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    /**
     * <p>Apply.</p>
     * @param builder
     * @param properties
     */
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.sqlserver.SqlServerConnector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMappers.whenNonNull();
        
        // Base connection configuration
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).when(Objects::nonNull).to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // Database and table filtering
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));

        // SQL Server specific configuration
        if (properties.getSqlServer() != null) {
            DebeziumConnectorProperties.SqlServer sqlServer = properties.getSqlServer();
            
            map.from(sqlServer::getDatabase).whenHasText().to(value -> builder.with("database.dbname", value));
            map.from(sqlServer::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(sqlServer::getSnapshotIsolationMode).whenHasText().to(value -> builder.with("snapshot.isolation.mode", value));
            
            // Other important configuration
            builder.with("database.encrypt", "false")
                   .with("database.trustServerCertificate", "true")
                   .with("database.applicationName", "DebeziumConnector")
                   .with("database.connectionTimeout", "30000")
                   .with("database.commandTimeout", "30000")
                   .with("database.loginTimeout", "30000");
            
            // Event processing configuration
            builder.with("tombstones.on.delete", "false")
                   .with("include.query", "false")
                   .with("database.initial.statements", "SET ARITHABORT ON; SET NUMERIC_ROUNDABORT OFF; SET CONCAT_NULL_YIELDS_NULL ON; SET ANSI_WARNINGS ON; SET ANSI_PADDING ON; SET ANSI_NULLS ON; SET QUOTED_IDENTIFIER ON;");
            
            // Performance optimisation configuration
            builder.with("poll.interval.ms", "1000")
                   .with("max.queue.size", "8192")
                   .with("max.batch.size", "2048")
                   .with("database.history.skip.unparseable.ddl", "true")
                   .with("database.history.store.only.monitored.tables.ddl", "true")
                   .with("database.history.store.only.captured.tables.ddl", "true");
        }
    }
}

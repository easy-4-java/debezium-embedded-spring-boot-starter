package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * {@link ConnectorConfigurer} for the Debezium Google Cloud Spanner connector.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class SpannerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.spanner.SpannerConnector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // Base connection configuration
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // Database and table filtering
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        
        // Spanner specific configuration
        if (properties.getSpanner() != null) {
            DebeziumConnectorProperties.Spanner spanner = properties.getSpanner();
            map.from(spanner::getConnectionString).whenHasText().to(value -> builder.with("spanner.connection.string", value));
            map.from(spanner::getDatabaseList).whenHasText().to(value -> builder.with("database.include.list", value));
            map.from(spanner::getTableList).whenHasText().to(value -> builder.with("table.include.list", value));
            map.from(spanner::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(spanner::getProjectId).whenHasText().to(value -> builder.with("spanner.project.id", value));
            map.from(spanner::getInstanceId).whenHasText().to(value -> builder.with("spanner.instance.id", value));
            map.from(spanner::getDatabaseId).whenHasText().to(value -> builder.with("spanner.database.id", value));
            
            // Event processing configuration
            map.from(spanner::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(spanner::getIncludeQuery).to(value -> builder.with("include.query", value));
            
            // Performance optimisation configuration
            map.from(spanner::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(spanner::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(spanner::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        }
    }
}

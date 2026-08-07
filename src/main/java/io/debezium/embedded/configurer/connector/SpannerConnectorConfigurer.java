package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Spanner 连接器配置器。
 */
public class SpannerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.spanner.SpannerConnector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础连接配置
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        
        // Spanner 特定配置
        if (properties.getSpanner() != null) {
            DebeziumConnectorProperties.Spanner spanner = properties.getSpanner();
            map.from(spanner::getConnectionString).whenHasText().to(value -> builder.with("spanner.connection.string", value));
            map.from(spanner::getDatabaseList).whenHasText().to(value -> builder.with("database.include.list", value));
            map.from(spanner::getTableList).whenHasText().to(value -> builder.with("table.include.list", value));
            map.from(spanner::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(spanner::getProjectId).whenHasText().to(value -> builder.with("spanner.project.id", value));
            map.from(spanner::getInstanceId).whenHasText().to(value -> builder.with("spanner.instance.id", value));
            map.from(spanner::getDatabaseId).whenHasText().to(value -> builder.with("spanner.database.id", value));
            
            // 事件处理配置
            map.from(spanner::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(spanner::getIncludeQuery).to(value -> builder.with("include.query", value));
            
            // 性能优化配置
            map.from(spanner::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(spanner::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(spanner::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        }
    }
}

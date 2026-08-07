package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Cassandra 连接器配置器。
 */
public class CassandraConnectorConfigurer implements ConnectorConfigurer {

    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.cassandra.CassandraConnector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础连接配置
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("keyspace.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        
        // Cassandra 特定配置
        if (properties.getCassandra() != null) {
            DebeziumConnectorProperties.Cassandra cassandra = properties.getCassandra();
            map.from(cassandra::getConnectionString).whenHasText().to(value -> builder.with("cassandra.connection.string", value));
            
            // 如果没有连接字符串，使用传统的连接方式
            if (cassandra.getConnectionString() == null || cassandra.getConnectionString().trim().isEmpty()) {
                map.from(properties::getHost).whenHasText().to(host -> 
                    map.from(properties::getPort).whenNonNull().to(port -> 
                        builder.with("cassandra.hosts", host + ":" + port)
                    )
                );
                map.from(properties::getUsername).whenHasText().to(value -> builder.with("cassandra.user", value));
                map.from(properties::getPassword).whenHasText().to(value -> builder.with("cassandra.password", value));
            }
            
            map.from(cassandra::getDatabaseList).whenHasText().to(value -> builder.with("keyspace.include.list", value));
            map.from(cassandra::getTableList).whenHasText().to(value -> builder.with("table.include.list", value));
            map.from(cassandra::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            
            // 连接配置
            map.from(cassandra::getConnectTimeoutMs).to(value -> builder.with("cassandra.connect.timeout.ms", value));
            map.from(cassandra::getReadTimeoutMs).to(value -> builder.with("cassandra.read.timeout.ms", value));
            
            // 事件处理配置
            map.from(cassandra::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(cassandra::getIncludeQuery).to(value -> builder.with("include.query", value));
            
            // 性能优化配置
            map.from(cassandra::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(cassandra::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(cassandra::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        }
    }
}

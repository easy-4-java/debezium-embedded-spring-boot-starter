package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MongoDB 连接器配置器。
 */
public class MongoDbConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector");
        
        // MongoDB 特定配置
        if (properties.getMongoDb() != null) {
            DebeziumConnectorProperties.MongoDb mongoDb = properties.getMongoDb();
            
            /*
             * 批量设置参数
             */
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            
            // 基础连接配置
            map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
            
            // 连接配置
            map.from(mongoDb::getConnectionString).whenHasText().to(value -> builder.with("mongodb.connection.string", value));
            map.from(mongoDb::getAuthSource).whenHasText().to(value -> builder.with("mongodb.authsource", value));
            
            // 如果没有连接字符串，使用传统的连接方式
            if (mongoDb.getConnectionString() == null || mongoDb.getConnectionString().trim().isEmpty()) {
                map.from(properties::getHost).whenHasText().to(host -> 
                    map.from(properties::getPort).whenNonNull().to(port -> 
                        builder.with("mongodb.hosts", host + ":" + port)
                    )
                );
                map.from(properties::getUsername).whenHasText().to(value -> builder.with("mongodb.user", value));
                map.from(properties::getPassword).whenHasText().to(value -> builder.with("mongodb.password", value));
            }
            
            // 数据库和集合过滤
            map.from(mongoDb::getDatabaseList).whenHasText().to(value -> builder.with("database.include.list", value));
            map.from(mongoDb::getCollectionList).whenHasText().to(value -> builder.with("collection.include.list", value));
            
            // 快照配置
            map.from(mongoDb::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            
            // 连接超时配置
            map.from(mongoDb::getConnectTimeoutMs).to(value -> builder.with("mongodb.connect.timeout.ms", value));
            map.from(mongoDb::getSocketTimeoutMs).to(value -> builder.with("mongodb.socket.timeout.ms", value));
            map.from(mongoDb::getServerSelectionTimeoutMs).to(value -> builder.with("mongodb.server.selection.timeout.ms", value));
            
            // 连接池配置
            map.from(mongoDb::getMaxConnectionPoolSize).to(value -> builder.with("mongodb.max.connection.pool.size", value));
            map.from(mongoDb::getMinConnectionPoolSize).to(value -> builder.with("mongodb.min.connection.pool.size", value));
            map.from(mongoDb::getMaxConnectionIdleTimeMs).to(value -> builder.with("mongodb.max.connection.idle.time.ms", value));
            map.from(mongoDb::getMaxConnectionLifeTimeMs).to(value -> builder.with("mongodb.max.connection.life.time.ms", value));
            
            // 事件处理配置
            map.from(mongoDb::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(mongoDb::getIncludeQuery).to(value -> builder.with("include.query", value));
            map.from(mongoDb::getFieldRenames).whenHasText().to(value -> builder.with("field.renames", value));
            map.from(mongoDb::getFieldExcludeList).whenHasText().to(value -> builder.with("field.exclude.list", value));
            
            // 性能优化配置
            map.from(mongoDb::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(mongoDb::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(mongoDb::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
            map.from(mongoDb::getMaxQueueSizeInBytes).to(value -> builder.with("max.queue.size.in.bytes", value));
        }
    }
}

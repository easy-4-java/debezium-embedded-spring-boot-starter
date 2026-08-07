package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Oracle 连接器配置器。
 */
public class OracleConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.oracle.OracleConnector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础连接配置
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));

        // Oracle 特定配置
        if (properties.getOracle() != null) {
            DebeziumConnectorProperties.Oracle oracle = properties.getOracle();
            
            map.from(oracle::getDatabase).whenHasText().to(value -> builder.with("database.dbname", value));
            map.from(oracle::getPdbName).whenHasText().to(value -> builder.with("database.pdb.name", value));
            map.from(oracle::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(oracle::getLogMiningStrategy).whenHasText().to(value -> builder.with("log.mining.strategy", value));
            
            // 其他重要配置
            builder.with("database.connection.adapter", "logminer")
                   .with("database.oracle.version", "19")
                   .with("database.oracle.connection.pool.size", "20")
                   .with("database.oracle.connection.pool.increment", "5")
                   .with("database.oracle.connection.pool.max", "100")
                   .with("database.oracle.connection.pool.min", "5")
                   .with("database.oracle.connection.pool.timeout", "300")
                   .with("database.oracle.connection.pool.validate", "true");
            
            // 事件处理配置
            builder.with("tombstones.on.delete", "false")
                   .with("include.query", "false")
                   .with("database.initial.statements", "ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
            
            // 性能优化配置
            builder.with("poll.interval.ms", "1000")
                   .with("max.queue.size", "8192")
                   .with("max.batch.size", "2048")
                   .with("log.mining.batch.size.min", "1")
                   .with("log.mining.batch.size.max", "1000")
                   .with("log.mining.batch.size.default", "250");
        }
    }
}

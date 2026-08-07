package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MariaDB 连接器配置器。
 */
public class MariaDbConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
               .with("include.schema.changes", "false");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础连接配置
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerId).whenHasText().to(value -> builder.with("database.server.id", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with("database.exclude.list", value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with("table.exclude.list", value));

        // MariaDB 特定配置
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();
            map.from(mySql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(mySql::getSnapshotLockingMode).whenHasText().to(value -> builder.with("snapshot.locking.mode", value));
            map.from(mySql::getConnectTimeoutMs).to(value -> builder.with("connect.timeout.ms", value));
            map.from(mySql::getGtidSourceFilterDmlEvents).to(value -> builder.with("gtid.source.filter.dml.events", value));
        }
    }
}

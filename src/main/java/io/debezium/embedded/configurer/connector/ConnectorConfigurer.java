package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;

/**
 * 抽象的数据库连接器配置器，将数据库特定的配置写入 Debezium Configuration.Builder。
 */
public interface ConnectorConfigurer {
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    void apply(Configuration.Builder builder, DebeziumConnectorProperties properties);
}



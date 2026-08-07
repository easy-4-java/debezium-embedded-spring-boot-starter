package io.debezium.embedded.configurer.connector;

import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;

/**
 * 连接器配置器工厂。
 * 根据配置的连接器类型返回相应的配置器实现。
 */
public class ConnectorConfigurerFactory {

    /**
     * 根据配置的连接器类型返回相应的配置器实现。
     *
     * @param properties 连接器配置属性
     * @return 连接器配置器
     */
    public static ConnectorConfigurer from(DebeziumConnectorProperties properties) {
        switch (properties.getType()) {
            case MYSQL:
                return new MySqlConnectorConfigurer();
            case MARIADB:
                return new MariaDbConnectorConfigurer();
            case POSTGRESQL:
                return new PostgreSqlConnectorConfigurer();
            case MONGODB:
                return new MongoDbConnectorConfigurer();
            case ORACLE:
                return new OracleConnectorConfigurer();
            case SQLSERVER:
                return new SqlServerConnectorConfigurer();
            case DB2:
                return new Db2ConnectorConfigurer();
            case CASSANDRA:
                return new CassandraConnectorConfigurer();
            case VITESS:
                return new VitessConnectorConfigurer();
            case SPANNER:
                return new SpannerConnectorConfigurer();
            case INFORMIX:
                return new InformixConnectorConfigurer();
            case CUSTOM:
                return new CustomConnectorConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported connector type: " + properties.getType());
        }
    }
}

package io.debezium.embedded.configurer.connector;

import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;

/**
 * Factory that resolves the {@link ConnectorConfigurer} implementation matching
 * the connector type declared on the supplied properties.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class ConnectorConfigurerFactory {

    /**
     * Returns the {@link ConnectorConfigurer} for the connector type carried by
     * {@code properties}.
     *
     * @param properties the connector configuration properties
     * @return the matching connector configurer
     * @throws IllegalArgumentException if the connector type is not supported
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

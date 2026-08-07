package io.debezium.embedded.configurer.connector;

/**
 * 数据库连接器类型。
 * 支持主流数据库的 Debezium 连接器。
 */
public enum ConnectorType {
    /**
     * MySQL 连接器
     */
    MYSQL,
    /**
     * MariaDB 连接器
     */
    MARIADB,
    /**
     * MongoDB 连接器
     */
    MONGODB,
    /**
     * Oracle 连接器
     */
    ORACLE,
    /**
     * PostgreSQL 连接器
     */
    POSTGRESQL,
    /**
     * SqlServer 连接器
     */
    SQLSERVER,
    /**
     * DB2 连接器
     */
    DB2,
    /**
     * Cassandra 连接器
     */
    CASSANDRA,
    /**
     * Vitess 连接器
     */
    VITESS,
    /**
     * Spanner 连接器
     */
    SPANNER,
    /**
     * Informix 连接器
     */
    INFORMIX,
    /**
     * 自定义连接器
     */
    CUSTOM
}

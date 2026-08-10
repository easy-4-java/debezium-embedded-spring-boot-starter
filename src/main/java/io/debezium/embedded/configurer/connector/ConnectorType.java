package io.debezium.embedded.configurer.connector;

/**
 * Enumeration of the database connector types supported by this starter.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public enum ConnectorType {
    /** MySQL connector. */
    MYSQL,
    /** MariaDB connector. */
    MARIADB,
    /** MongoDB connector. */
    MONGODB,
    /** Oracle connector. */
    ORACLE,
    /** PostgreSQL connector. */
    POSTGRESQL,
    /** Microsoft SQL Server connector. */
    SQLSERVER,
    /** IBM Db2 connector. */
    DB2,
    /** Cassandra connector. */
    CASSANDRA,
    /** Vitess connector. */
    VITESS,
    /** Google Cloud Spanner connector. */
    SPANNER,
    /** IBM Informix connector. */
    INFORMIX,
    /** User-provided custom connector. */
    CUSTOM
}

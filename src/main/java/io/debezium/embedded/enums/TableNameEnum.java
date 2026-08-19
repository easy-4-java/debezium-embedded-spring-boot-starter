package io.debezium.embedded.enums;

import java.util.StringJoiner;

/**
 * Table name combination constants used when matching handlers to tables.
 * <p>The {@link #ALL} wildcard matches any destination/schema/table.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public enum TableNameEnum {

    /** Wildcard matching any destination, schema and table. */
    ALL("*", "*", "*");

    /** Delimiter used to combine destination/schema/table names. */
    public static final CharSequence DELIMITER = ".";

    String destination;
    String schema;
    String table;

    TableNameEnum(String destination, String schema, String table) {
        this.destination = destination;
        this.schema = schema;
        this.table = table;
    }

    /** @return the destination component of this combination. */
    public String getDestination() {
        return destination;
    }

    /** @return the schema component of this combination. */
    public String getSchema() {
        return schema;
    }

    /** @return the table component of this combination. */
    public String getTable() {
        return table;
    }

    /** @return the {@code schema.table} representation of this combination. */
    @Override
    /**
     * <p>To string.</p>
     * @return the result
     */
    public String toString() {
        StringJoiner joiner = new StringJoiner(".").add(schema).add(table);
        return joiner.toString();
    }

}

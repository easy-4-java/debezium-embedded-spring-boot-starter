package io.debezium.embedded.annotation;

import java.lang.annotation.*;

/**
 * Binds a {@link io.debezium.embedded.handler.RecordChangeEventEntryHandler} to
 * a specific destination/schema/table combination.
 * <p>Defaults ({@code *} / empty) act as wildcards that match any value.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * <p>Auto-configuration for DebeziumTable.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public @interface DebeziumTable {

    /**
     * Connector destination name. Defaults to empty, meaning any destination.
     *
     * @return the destination name
     */
    String destination() default "";

    /**
     * Database schema name. Defaults to {@code *}, meaning any schema.
     *
     * @return the schema name
     */
    String schema() default "*";

    /**
     * Table name to listen on. Defaults to {@code *}, meaning any table.
     *
     * @return the table name
     */
    String table() default "*";

}

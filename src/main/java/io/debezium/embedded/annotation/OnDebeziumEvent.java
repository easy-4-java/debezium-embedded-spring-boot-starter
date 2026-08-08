package io.debezium.embedded.annotation;


import io.debezium.embedded.protocol.DebeziumEntry;

import java.lang.annotation.*;

/**
 * Marks an annotated method or class as a listener for Debezium change events.
 * <p>The destination, schema, table and event type selectors can be combined
 * to narrow the set of events the listener accepts.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnDebeziumEvent {

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

    /**
     * Event types the listener accepts. Defaults to none, meaning all event types.
     *
     * @return the accepted {@link DebeziumEntry.EventType}s
     */
    DebeziumEntry.EventType[] eventType();

}

package io.debezium.embedded.annotation.event;

import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Listener for {@link DebeziumEntry.EventType#CREATE} events.
 * <p>Methods annotated with this are invoked when a new record is inserted
 * into the matching table.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(eventType = DebeziumEntry.EventType.CREATE)
public @interface OnInsertEvent {

    /**
     * Connector destination name. Defaults to empty, meaning any destination.
     *
     * @return the destination name
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String destination() default "";

    /**
     * Database schema name to match.
     *
     * @return the schema name
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String schema();

    /**
     * Table name to listen on. Defaults to any table.
     *
     * @return the table name
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String table();

}

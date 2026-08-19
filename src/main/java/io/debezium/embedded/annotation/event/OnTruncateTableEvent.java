package io.debezium.embedded.annotation.event;

import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Listener for {@link DebeziumEntry.EventType#TRUNCATE} events.
 * <p>Methods annotated with this are invoked when a matching table is
 * truncated.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(eventType = DebeziumEntry.EventType.TRUNCATE)
/**
 * <p>Auto-configuration for OnTruncateTableEvent.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public @interface OnTruncateTableEvent {
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
}

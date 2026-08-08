package io.debezium.embedded.handler;

import io.debezium.engine.ChangeEvent;

import java.util.Properties;

/**
 * Functional handler for a single JSON {@link ChangeEvent} emitted by the engine.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@FunctionalInterface
public interface ChangeEventHandler {

    /**
     * Handles a single change event.
     *
     * @param changeEvent the change event to process
     * @param props       the Debezium engine configuration as properties
     */
    void handleEvent(ChangeEvent<String, String> changeEvent, Properties props);

}

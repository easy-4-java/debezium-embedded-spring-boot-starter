package io.debezium.embedded.handler;

import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;
import java.util.Properties;

/**
 * Functional handler for a batch of {@link RecordChangeEvent}s emitted by the engine.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@FunctionalInterface
public interface RecordChangeEventHandler {

    /**
     * Handles a batch of record change events and acknowledges them through
     * the supplied committer.
     *
     * @param recordChangeEvents the batch of record change events to process
     * @param recordCommitter    the committer used to acknowledge processed records
     * @param props              the Debezium engine configuration as properties
     */
    void handleEvent(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                     DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter,
                     Properties props);

}

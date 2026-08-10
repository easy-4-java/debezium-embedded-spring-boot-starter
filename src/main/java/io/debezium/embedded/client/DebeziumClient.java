package io.debezium.embedded.client;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.context.SmartLifecycle;

import java.util.List;

/**
 * Contract for a managed Debezium client that owns one or more embedded engines.
 * <p>
 * Extends Spring's {@link SmartLifecycle} so the engine(s) are started and
 * stopped together with the application context.
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface DebeziumClient extends SmartLifecycle {

    /**
     * Handles a single JSON {@link ChangeEvent} emitted by the engine.
     *
     * @param changeEvent the change event to process
     */
    void process(ChangeEvent<String, String> changeEvent);

    /**
     * Handles a batch of {@link RecordChangeEvent}s and commits their offsets.
     *
     * @param recordChangeEvents the batch of record-change events to process
     * @param recordCommitter    the committer used to acknowledge processed records
     */
    void process(List<RecordChangeEvent<SourceRecord>> recordChangeEvents, DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter);

}

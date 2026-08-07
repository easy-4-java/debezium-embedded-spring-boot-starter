package io.debezium.embedded.client;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.context.SmartLifecycle;

import java.util.List;

/**
 * Debezium Client 接口
 */
public interface DebeziumClient extends SmartLifecycle {

    /**
     * 处理 ChangeEvent
     */
    void process(ChangeEvent<String, String> changeEvent);

    /**
     * 处理 RecordChangeEvent
     */
    void process(List<RecordChangeEvent<SourceRecord>> recordChangeEvents, DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter);

}

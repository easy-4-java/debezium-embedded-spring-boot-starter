package io.debezium.embedded.handler;

import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;
import java.util.Properties;

/**
 * 消息处理器
 */
@FunctionalInterface
public interface RecordChangeEventHandler {

    /**
     * 处理消息
     * @param recordChangeEvents 数据变动事件对象集合
     * @param recordCommitter 的
     * @param props 配置
     */
    void handleEvent(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                     DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter,
                     Properties props);

}

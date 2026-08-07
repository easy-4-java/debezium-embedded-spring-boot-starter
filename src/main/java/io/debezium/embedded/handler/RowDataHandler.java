package io.debezium.embedded.handler;

import io.debezium.embedded.protocol.DebeziumEntry;

/**
 * 处理行数据
 * @param <T> 行数据
 */
public interface RowDataHandler<T> {

    <R> void handlerRowData(T t, RecordChangeEventEntryHandler<R> entryHandler, DebeziumEntry.EventType eventType) throws Exception;

}

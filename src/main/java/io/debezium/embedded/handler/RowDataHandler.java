package io.debezium.embedded.handler;

import io.debezium.embedded.protocol.DebeziumEntry;

/**
 * Strategy for converting and dispatching a single row change to a
 * {@link RecordChangeEventEntryHandler}.
 *
 * @param <T> the raw row payload type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface RowDataHandler<T> {

    /**
     * Materialises the row payload and dispatches it to the appropriate
     * callback on {@code entryHandler} for the given {@code eventType}.
     *
     * @param t            the raw row payload
     * @param entryHandler the callback to invoke
     * @param eventType    the change event type (insert/update/delete)
     * @param <R>          the row model type
     * @throws Exception if materialisation or dispatch fails
     */
    <R> void handlerRowData(T t, RecordChangeEventEntryHandler<R> entryHandler, DebeziumEntry.EventType eventType) throws Exception;

}

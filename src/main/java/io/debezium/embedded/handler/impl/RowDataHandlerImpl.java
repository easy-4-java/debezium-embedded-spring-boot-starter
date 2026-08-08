package io.debezium.embedded.handler.impl;


import io.debezium.embedded.factory.IModelFactory;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.protocol.DebeziumEntry;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link RowDataHandler} backed by the {@code DebeziumEntry.RowData} protocol
 * type, typically used for binary protocol events.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class RowDataHandlerImpl implements RowDataHandler<DebeziumEntry.RowData> {


    /** Factory used to convert raw columns into the handler's row model. */
    private IModelFactory<List<DebeziumEntry.Column>> modelFactory;

    /**
     * Creates a new handler using the supplied model factory.
     *
     * @param modelFactory factory used to materialise rows
     */
    public RowDataHandlerImpl(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Dispatches the supplied row payload to the matching callback.
     *
     * @param rowData       the raw row payload
     * @param entryHandler  the callback to invoke
     * @param eventType     the change event type
     * @param <R>           the row model type
     * @throws Exception if materialisation or dispatch fails
     */
    @Override
    public <R> void handlerRowData(DebeziumEntry.RowData rowData, RecordChangeEventEntryHandler<R> entryHandler, DebeziumEntry.EventType eventType) throws Exception {
        if (Objects.isNull(rowData) || Objects.isNull(entryHandler) || Objects.isNull(eventType)) {
            return;
        }
        switch (eventType) {
            case CREATE:
                R object = modelFactory.newInstance(entryHandler, rowData.getAfterColumnsList());
                entryHandler.insert(object);
                break;
            case UPDATE:
                Set<String> updateColumnSet = rowData.getAfterColumnsList().stream().filter(DebeziumEntry.Column::getUpdated)
                        .map(DebeziumEntry.Column::getName).collect(Collectors.toSet());
                R before = modelFactory.newInstance(entryHandler, rowData.getBeforeColumnsList(),updateColumnSet);
                R after = modelFactory.newInstance(entryHandler, rowData.getAfterColumnsList());
                entryHandler.update(before, after);
                break;
            case DELETE:
                R o = modelFactory.newInstance(entryHandler, rowData.getBeforeColumnsList());
                entryHandler.delete(o);
                break;
            default:
                break;
        }
    }
}

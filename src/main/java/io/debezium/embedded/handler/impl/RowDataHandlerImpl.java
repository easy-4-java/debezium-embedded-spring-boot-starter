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
 * 处理行数据
 */
public class RowDataHandlerImpl implements RowDataHandler<DebeziumEntry.RowData> {


    private IModelFactory<List<DebeziumEntry.Column>> modelFactory;

    public RowDataHandlerImpl(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

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

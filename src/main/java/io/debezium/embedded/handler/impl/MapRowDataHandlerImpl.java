package io.debezium.embedded.handler.impl;


import io.debezium.embedded.factory.IModelFactory;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.protocol.DebeziumEntry;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapRowDataHandlerImpl implements RowDataHandler<List<Map<String, String>>> {

    private IModelFactory<Map<String,String>> modelFactory;

    public MapRowDataHandlerImpl(IModelFactory<Map<String, String>> modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public <R> void handlerRowData(List<Map<String, String>> list, RecordChangeEventEntryHandler<R> entryHandler, DebeziumEntry.EventType eventType) throws Exception{
        if (Objects.isNull(list) || Objects.isNull(entryHandler) || Objects.isNull(eventType)) {
            return;
        }
        switch (eventType) {
            case CREATE:
                R entry  = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.insert(entry);
                break;
            case UPDATE:
                R before = modelFactory.newInstance(entryHandler, list.get(1));
                R after = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.update(before, after);
                break;
            case DELETE:
                R o = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.delete(o);
                break;
            default:
                break;
        }
    }
}

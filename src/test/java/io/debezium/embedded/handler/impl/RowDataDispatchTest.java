package io.debezium.embedded.handler.impl;

import io.debezium.embedded.factory.IModelFactory;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RowDataDispatchTest {
    private static class CapturingHandler implements RecordChangeEventEntryHandler<String> {
        String inserted;
        String before;
        String after;
        String deleted;
        public void insert(String value) { inserted = value; }
        public void update(String oldValue, String newValue) { before = oldValue; after = newValue; }
        public void delete(String value) { deleted = value; }
    }

    @Test
    void mapRowsDispatchCreateUpdateAndDelete() throws Exception {
        IModelFactory<Map<String, String>> factory = new IModelFactory<>() {
            public <R> R newInstance(RecordChangeEventEntryHandler handler, Map<String, String> row) {
                return (R) row.get("id");
            }
        };
        MapRowDataHandlerImpl dispatcher = new MapRowDataHandlerImpl(factory);
        CapturingHandler handler = new CapturingHandler();
        dispatcher.handlerRowData(null, handler, DebeziumEntry.EventType.CREATE);
        assertNull(handler.inserted);
        dispatcher.handlerRowData(List.of(Map.of("id", "new")), handler, DebeziumEntry.EventType.CREATE);
        dispatcher.handlerRowData(List.of(Map.of("id", "new"), Map.of("id", "old")), handler, DebeziumEntry.EventType.UPDATE);
        dispatcher.handlerRowData(List.of(Map.of("id", "old")), handler, DebeziumEntry.EventType.DELETE);
        assertEquals("new", handler.inserted);
        assertEquals("old", handler.before);
        assertEquals("new", handler.after);
        assertEquals("old", handler.deleted);
    }

    @Test
    void protocolRowsDispatchCreateUpdateAndDelete() throws Exception {
        IModelFactory<List<DebeziumEntry.Column>> factory = new IModelFactory<>() {
            public <R> R newInstance(RecordChangeEventEntryHandler handler, List<DebeziumEntry.Column> columns) {
                return (R) columns.get(0).getValue();
            }
            public <R> R newInstance(RecordChangeEventEntryHandler handler, List<DebeziumEntry.Column> columns, Set<String> updated) {
                assertEquals(Set.of("id"), updated);
                return newInstance(handler, columns);
            }
        };
        RowDataHandlerImpl dispatcher = new RowDataHandlerImpl(factory);
        CapturingHandler handler = new CapturingHandler();
        DebeziumEntry.Column before = new DebeziumEntry.Column();
        before.setName("id"); before.setValue("old"); before.setUpdated(false);
        DebeziumEntry.Column after = new DebeziumEntry.Column();
        after.setName("id"); after.setValue("new"); after.setUpdated(true);
        DebeziumEntry.RowData row = new DebeziumEntry.RowData();
        row.setBeforeColumnsList(List.of(before)); row.setAfterColumnsList(List.of(after));
        dispatcher.handlerRowData(null, handler, DebeziumEntry.EventType.CREATE);
        assertNull(handler.inserted);
        dispatcher.handlerRowData(row, handler, DebeziumEntry.EventType.CREATE);
        dispatcher.handlerRowData(row, handler, DebeziumEntry.EventType.UPDATE);
        dispatcher.handlerRowData(row, handler, DebeziumEntry.EventType.DELETE);
        assertEquals("new", handler.inserted);
        assertEquals("old", handler.before);
        assertEquals("new", handler.after);
        assertEquals("old", handler.deleted);
    }
}

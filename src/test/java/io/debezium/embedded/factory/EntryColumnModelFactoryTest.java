package io.debezium.embedded.factory;

import io.debezium.embedded.annotation.DebeziumTable;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntryColumnModelFactoryTest {
    private final EntryColumnModelFactory factory = new EntryColumnModelFactory();
    private final AllTablesHandler handler = new AllTablesHandler();

    @Test
    void wildcardHandlerReceivesEveryColumn() throws Exception {
        Map<String, String> row = factory.newInstance(handler,
                List.of(column("id", "7"), column("name", "widget")));

        assertEquals(Map.of("id", "7", "name", "widget"), row);
    }

    @Test
    void wildcardHandlerFiltersUnchangedColumnsForUpdates() throws Exception {
        Map<String, String> row = factory.newInstance(handler,
                List.of(column("id", "7"), column("name", "widget")), Set.of("name"));

        assertEquals(Map.of("name", "widget"), row);
    }

    @Test
    void emptyEntityColumnsProduceNoModel() throws Exception {
        assertNull(factory.newInstance(String.class, List.of()));
    }

    @Test
    void abstractFactoryPassesWildcardMapWithoutEntityMetadata() throws Exception {
        Map<String, String> row = Map.of("id", "7");
        assertEquals(row, new MapColumnModelFactory().newInstance(handler, row));
    }

    private static DebeziumEntry.Column column(String name, String value) {
        DebeziumEntry.Column column = new DebeziumEntry.Column();
        column.setName(name);
        column.setValue(value);
        return column;
    }

    @DebeziumTable
    private static class AllTablesHandler implements RecordChangeEventEntryHandler<Map<String, String>> {
    }
}

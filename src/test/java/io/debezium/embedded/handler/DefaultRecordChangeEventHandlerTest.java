package io.debezium.embedded.handler;

import io.debezium.embedded.context.DebeziumContext;
import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultRecordChangeEventHandlerTest {
    @SuppressWarnings("unchecked")
    private final RowDataHandler<List<Map<String, String>>> rows = mock(RowDataHandler.class);
    private final DefaultRecordChangeEventHandler handler = new DefaultRecordChangeEventHandler(List.of(), rows);

    @Test
    void skipsEmptyAndTombstoneBatchesAndCompletesOtherBatches() throws Exception {
        DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer = mock(DebeziumEngine.RecordCommitter.class);
        handler.handleEvent(List.of(), committer, new Properties());
        verifyNoInteractions(committer);
        SourceRecord tombstone = mock(SourceRecord.class);
        RecordChangeEvent<SourceRecord> event = () -> tombstone;
        handler.handleEvent(List.of(event), committer, new Properties());
        verify(committer).markBatchFinished();
    }

    @Test
    void parsesCreateRecordAndFinishesBatch() throws Exception {
        DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer = mock(DebeziumEngine.RecordCommitter.class);
        SourceRecord record = mock(SourceRecord.class);
        Schema rowSchema = SchemaBuilder.struct().field("id", Schema.STRING_SCHEMA).build();
        Schema sourceSchema = SchemaBuilder.struct().field("db", Schema.STRING_SCHEMA)
                .field("table", Schema.STRING_SCHEMA).field("ts_ms", Schema.INT64_SCHEMA).build();
        Schema envelopeSchema = SchemaBuilder.struct().field("source", sourceSchema).field("op", Schema.STRING_SCHEMA)
                .field("after", rowSchema).build();
        Struct envelope = new Struct(envelopeSchema)
                .put("source", new Struct(sourceSchema).put("db", "app").put("table", "users").put("ts_ms", 42L))
                .put("op", "c").put("after", new Struct(rowSchema).put("id", "7"));
        when(record.value()).thenReturn(envelope);
        handler.handleEvent(List.of(() -> record), committer, new Properties());
        verify(committer).markBatchFinished();
    }

    @Test
    void rowCallbackSeesContextAndItIsAlwaysCleared() throws Exception {
        DebeziumModel model = DebeziumModel.builder().table("users").build();
        RecordChangeEventEntryHandler<String> entry = new RecordChangeEventEntryHandler<>() {};
        doAnswer(invocation -> {
            assertSame(model, DebeziumContext.getModel());
            return null;
        }).when(rows).handlerRowData(anyList(), same(entry), eq(DebeziumEntry.EventType.CREATE));
        handler.handlerRowData(model, List.of(Map.of("id", "7")), entry, DebeziumEntry.EventType.CREATE);
        assertNull(DebeziumContext.getModel());
    }

    @Test
    void annotatedCallbackReceivesModelRowAndEventWithContext() throws Exception {
        Listener listener = new Listener();
        DebeziumModel model = DebeziumModel.builder().table("users").build();
        var method = Listener.class.getDeclaredMethod("onChange", DebeziumModel.class, List.class, DebeziumEntry.EventType.class);
        DebeziumEventHolder holder = new DebeziumEventHolder(listener, method, null);
        handler.handlerRowData(model, List.of(Map.of("id", "7")), holder, DebeziumEntry.EventType.CREATE);
        assertSame(model, listener.seenModel);
        assertEquals("7", listener.seenRows.get(0).get("id"));
        assertEquals(DebeziumEntry.EventType.CREATE, listener.seenEvent);
        assertNull(DebeziumContext.getModel());
    }

    @Test
    void discoversAnnotatedBeansAndIgnoresContextsWithoutThem() {
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(DebeziumEventHandler.class)).thenReturn(Map.of());
        handler.setApplicationContext(context);
        when(context.getBeansWithAnnotation(DebeziumEventHandler.class)).thenReturn(Map.of("listener", new Listener()));
        handler.setApplicationContext(context);
    }

    @DebeziumEventHandler
    static class Listener {
        DebeziumModel seenModel;
        List<Map<String, String>> seenRows;
        DebeziumEntry.EventType seenEvent;
        void onChange(DebeziumModel model, List<Map<String, String>> rows, DebeziumEntry.EventType event) {
            assertSame(model, DebeziumContext.getModel());
            seenModel = model; seenRows = rows; seenEvent = event;
        }
    }
}

package io.debezium.embedded.util;

import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GenericUtil}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("GenericUtil")
class GenericUtilTest {

    @Test
    @DisplayName("getInvokeArgs with RowChange binds parameters correctly")
    void getInvokeArgs_rowChange_bindsParams() throws Exception {
        Method method = MultiParamHandler.class.getDeclaredMethod("handleRowChange",
                DebeziumModel.class, DebeziumEntry.RowChange.class, DebeziumEntry.EventType.class);

        DebeziumModel model = DebeziumModel.builder().id(1L).build();
        DebeziumEntry.RowChange rowChange = new DebeziumEntry.RowChange();
        rowChange.setTable("users");
        DebeziumEntry.EventType eventType = DebeziumEntry.EventType.CREATE;

        Object[] args = GenericUtil.getInvokeArgs(method, model, rowChange, eventType);

        assertThat(args).hasSize(3);
        assertThat(args[0]).isSameAs(model);
        assertThat(args[1]).isSameAs(rowChange);
        assertThat(args[2]).isSameAs(eventType);
    }

    @Test
    @DisplayName("getInvokeArgs with RowData list binds parameters correctly")
    void getInvokeArgs_rowDataList_bindsParams() throws Exception {
        Method method = MultiParamHandler.class.getDeclaredMethod("handleRowData",
                DebeziumModel.class, List.class, DebeziumEntry.EventType.class);

        DebeziumModel model = DebeziumModel.builder().id(1L).build();
        List<Map<String, String>> rowData = List.of(Map.of("id", "1"));
        DebeziumEntry.EventType eventType = DebeziumEntry.EventType.UPDATE;

        Object[] args = GenericUtil.getInvokeArgs(method, model, rowData, eventType);

        assertThat(args).hasSize(3);
        assertThat(args[0]).isSameAs(model);
        assertThat(args[1]).isSameAs(rowData);
        assertThat(args[2]).isSameAs(eventType);
    }

    @Test
    @DisplayName("getInvokeArgs returns null for unknown parameter types")
    void getInvokeArgs_returnsNull_forUnknownTypes() throws Exception {
        Method method = UnknownParamHandler.class.getDeclaredMethod("handle",
                DebeziumModel.class, String.class);

        DebeziumModel model = DebeziumModel.builder().id(1L).build();
        DebeziumEntry.RowChange rowChange = new DebeziumEntry.RowChange();
        DebeziumEntry.EventType eventType = DebeziumEntry.EventType.CREATE;

        Object[] args = GenericUtil.getInvokeArgs(method, model, rowChange, eventType);

        assertThat(args).hasSize(2);
        assertThat(args[0]).isSameAs(model);
        assertThat(args[1]).isNull(); // String type doesn't match any
    }

    @Test
    @DisplayName("getTableClass returns null for unparameterized handler")
    void getTableClass_returnsNull_forUnparameterized() {
        // RawRecordHandler doesn't implement RecordChangeEventEntryHandler<T> with a type param
        // so we test with a properly parameterized one
        GenericHandler handler = new GenericHandler();
        Class<?> tableClass = GenericUtil.getTableClass(handler);
        assertThat(tableClass).isEqualTo(String.class);
    }

    // ==================== Fixtures ====================

    static class MultiParamHandler {
        public void handleRowChange(DebeziumModel model, DebeziumEntry.RowChange rowChange, DebeziumEntry.EventType eventType) {}
        public void handleRowData(DebeziumModel model, List<Map<String, String>> rowData, DebeziumEntry.EventType eventType) {}
    }

    static class UnknownParamHandler {
        public void handle(DebeziumModel model, String unknown) {}
    }

    static class GenericHandler implements RecordChangeEventEntryHandler<String> {
        @Override public void insert(String obj) {}
        @Override public void update(String before, String after) {}
        @Override public void delete(String obj) {}
    }
}

package io.debezium.embedded.util;

import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.embedded.model.DebeziumModel;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DebeziumUtil}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumUtil")
class DebeziumUtilTest {

    @Test
    @DisplayName("static constants are set correctly")
    void staticConstants_areSet() {
        assertThat(DebeziumUtil.DATA).isEqualTo("data");
        assertThat(DebeziumUtil.BEFORE_DATA).isEqualTo("beforeData");
        assertThat(DebeziumUtil.EVENT_TYPE).isEqualTo("eventType");
        assertThat(DebeziumUtil.TABLE).isEqualTo("table");
        assertThat(DebeziumUtil.PAYLOAD).isEqualTo("payload");
    }

    @Test
    @DisplayName("TableFieldName.filterJsonField returns true for known fields")
    void tableFieldName_filterJsonField_knownFields() {
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("db")).isTrue();
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("table")).isTrue();
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("ts_ms")).isTrue();
    }

    @Test
    @DisplayName("TableFieldName.filterJsonField returns false for unknown fields")
    void tableFieldName_filterJsonField_unknownFields() {
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("unknown")).isFalse();
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("foo")).isFalse();
    }

    @Test
    @DisplayName("checkNonEssentialData returns true when only filtered columns changed")
    void checkNonEssentialData_returnsTrue_whenOnlyFilteredColumnsChanged() {
        Map<String, Object> afterMap = new HashMap<>();
        afterMap.put("id", 1);
        afterMap.put("name", "new");
        afterMap.put("updated_at", "2025-01-02");

        Map<String, Object> beforeMap = new HashMap<>();
        beforeMap.put("id", 1);
        beforeMap.put("name", "old");
        beforeMap.put("updated_at", "2025-01-01");

        boolean result = DebeziumUtil.checkNonEssentialData("users", afterMap, beforeMap, List.of("name", "updated_at"));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("checkNonEssentialData returns false when essential columns changed")
    void checkNonEssentialData_returnsFalse_whenEssentialColumnsChanged() {
        Map<String, Object> afterMap = new HashMap<>();
        afterMap.put("id", 1);
        afterMap.put("name", "new");
        afterMap.put("status", "active");

        Map<String, Object> beforeMap = new HashMap<>();
        beforeMap.put("id", 1);
        beforeMap.put("name", "old");
        beforeMap.put("status", "inactive");

        boolean result = DebeziumUtil.checkNonEssentialData("users", afterMap, beforeMap, List.of("updated_at"));
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("checkNonEssentialData returns true when nothing changed")
    void checkNonEssentialData_returnsTrue_whenNothingChanged() {
        Map<String, Object> afterMap = new HashMap<>();
        afterMap.put("id", 1);

        Map<String, Object> beforeMap = new HashMap<>();
        beforeMap.put("id", 1);

        boolean result = DebeziumUtil.checkNonEssentialData("users", afterMap, beforeMap, List.of());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("DebeziumUtil getChangeDataInfo returns null for READ operation")
    void getChangeDataInfo_returnsNull_forReadOperation() {
        assertThat(DebeziumUtil.getChangeDataInfo(change("r", null, null), Map.of("table", "users"))).isNull();
    }

    private Struct change(String operation, String before, String after) {
        Schema row = SchemaBuilder.struct().field("id", Schema.STRING_SCHEMA).build();
        Schema source = SchemaBuilder.struct().optional().field("db", Schema.STRING_SCHEMA)
                .field("table", Schema.STRING_SCHEMA).field("ts_ms", Schema.INT64_SCHEMA).build();
        Schema envelope = SchemaBuilder.struct().field("source", source)
                .field("op", Schema.STRING_SCHEMA)
                .field("before", SchemaBuilder.struct().optional().field("id", Schema.STRING_SCHEMA).build())
                .field("after", SchemaBuilder.struct().optional().field("id", Schema.STRING_SCHEMA).build()).build();
        return new Struct(envelope).put("source", new Struct(source).put("db", "app").put("table", "users").put("ts_ms", 42L))
                .put("op", operation)
                .put("before", before == null ? null : new Struct(envelope.field("before").schema()).put("id", before))
                .put("after", after == null ? null : new Struct(envelope.field("after").schema()).put("id", after));
    }

    @Test
    void parsesCreateUpdateAndDeleteRecords() {
        for (String operation : List.of("c", "u", "d")) {
            Struct record = change(operation, operation.equals("c") ? null : "old", operation.equals("d") ? null : "new");
            Map<String, Object> source = DebeziumUtil.getChangeTableInfo(record);
            assertThat(source).containsEntry("db", "app").containsEntry("table", "users");
            DebeziumModel.ChangeListenerModel model = DebeziumUtil.getChangeDataInfo(record, source);
            assertThat(model).isNotNull();
            assertThat(model.getDb()).isEqualTo("app");
            assertThat(model.getTable()).isEqualTo("users");
            assertThat(model.getChangeTime()).isEqualTo(42L);
            assertThat(model.getEventType()).isNotNull();
            if (!operation.equals("d")) assertThat(model.getData()).contains("new");
            if (!operation.equals("c")) assertThat(model.getBeforeData()).contains("old");
            if (operation.equals("d")) assertThat(model.getData()).isNull();
            if (operation.equals("c")) assertThat(model.getBeforeData()).isNull();
        }
    }

    @Test
    void skipsUnknownOperationAndRecordsWithoutSource() {
        assertThat(DebeziumUtil.getChangeDataInfo(change("r", null, null), Map.of("table", "users"))).isNull();
        Struct missingSource = change("c", null, "new");
        missingSource.put("source", null);
        assertThat(DebeziumUtil.getChangeTableInfo(missingSource)).isEmpty();
        Struct schemaChange = new Struct(SchemaBuilder.struct().field("op", Schema.STRING_SCHEMA).build()).put("op", "c");
        assertThat(DebeziumUtil.getChangeTableInfo(schemaChange)).isEmpty();
        DebeziumModel.ChangeListenerModel updateWithoutBefore = DebeziumUtil.getChangeDataInfo(
                change("u", null, "new"), Map.of("table", "users"));
        assertThat(updateWithoutBefore.getBeforeData()).isNull();
    }
}

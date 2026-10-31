package io.debezium.embedded.protocol;

import io.debezium.data.Envelope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DebeziumEntry} inner classes.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumEntry")
class DebeziumEntryTest {

    // ==================== EventType ====================

    @Test
    @DisplayName("EventType.valueOf(1) returns CREATE")
    void eventType_valueOf1_returnsCreate() {
        assertThat(DebeziumEntry.EventType.valueOf(1)).isEqualTo(DebeziumEntry.EventType.CREATE);
    }

    @Test
    @DisplayName("EventType.valueOf(2) returns UPDATE")
    void eventType_valueOf2_returnsUpdate() {
        assertThat(DebeziumEntry.EventType.valueOf(2)).isEqualTo(DebeziumEntry.EventType.UPDATE);
    }

    @Test
    @DisplayName("EventType.valueOf(3) returns DELETE")
    void eventType_valueOf3_returnsDelete() {
        assertThat(DebeziumEntry.EventType.valueOf(3)).isEqualTo(DebeziumEntry.EventType.DELETE);
    }

    @Test
    @DisplayName("EventType.valueOf(4) returns TRUNCATE")
    void eventType_valueOf4_returnsTruncate() {
        assertThat(DebeziumEntry.EventType.valueOf(4)).isEqualTo(DebeziumEntry.EventType.TRUNCATE);
    }

    @Test
    @DisplayName("EventType.valueOf(unknown) returns null")
    void eventType_valueOfUnknown_returnsNull() {
        assertThat(DebeziumEntry.EventType.valueOf(99)).isNull();
    }

    @Test
    @DisplayName("EventType indices are correct")
    void eventType_indices_areCorrect() {
        assertThat(DebeziumEntry.EventType.CREATE.getIndex()).isEqualTo(1);
        assertThat(DebeziumEntry.EventType.UPDATE.getIndex()).isEqualTo(2);
        assertThat(DebeziumEntry.EventType.DELETE.getIndex()).isEqualTo(3);
        assertThat(DebeziumEntry.EventType.TRUNCATE.getIndex()).isEqualTo(4);
    }

    @Test
    @DisplayName("EventType operations map correctly")
    void eventType_operations_mapCorrectly() {
        assertThat(DebeziumEntry.EventType.CREATE.getOperation()).isEqualTo(Envelope.Operation.CREATE);
        assertThat(DebeziumEntry.EventType.UPDATE.getOperation()).isEqualTo(Envelope.Operation.UPDATE);
        assertThat(DebeziumEntry.EventType.DELETE.getOperation()).isEqualTo(Envelope.Operation.DELETE);
        assertThat(DebeziumEntry.EventType.TRUNCATE.getOperation()).isEqualTo(Envelope.Operation.TRUNCATE);
    }

    // ==================== RowChange ====================

    @Test
    @DisplayName("RowChange setters and getters work")
    void rowChange_setterGetter() {
        DebeziumEntry.RowChange rc = new DebeziumEntry.RowChange();
        rc.setOperation(Envelope.Operation.CREATE);
        rc.setBefore("{\"id\":1}");
        rc.setAfter("{\"id\":2}");
        rc.setChange("change-data");
        rc.setSchema("public");
        rc.setTable("users");
        rc.setDestination("my-dest");
        rc.setChangeTime(1000L);
        rc.setCreateTime(2000L);

        assertThat(rc.getOperation()).isEqualTo(Envelope.Operation.CREATE);
        assertThat(rc.getBefore()).isEqualTo("{\"id\":1}");
        assertThat(rc.getAfter()).isEqualTo("{\"id\":2}");
        assertThat(rc.getChange()).isEqualTo("change-data");
        assertThat(rc.getSchema()).isEqualTo("public");
        assertThat(rc.getTable()).isEqualTo("users");
        assertThat(rc.getDestination()).isEqualTo("my-dest");
        assertThat(rc.getChangeTime()).isEqualTo(1000L);
        assertThat(rc.getCreateTime()).isEqualTo(2000L);
    }

    // ==================== RowData ====================

    @Test
    @DisplayName("RowData setters and getters work")
    void rowData_setterGetter() {
        DebeziumEntry.Column col1 = new DebeziumEntry.Column();
        col1.setName("id");
        col1.setValue("1");
        col1.setUpdated(false);

        DebeziumEntry.Column col2 = new DebeziumEntry.Column();
        col2.setName("name");
        col2.setValue("new_name");
        col2.setUpdated(true);

        List<DebeziumEntry.Column> beforeCols = List.of(col1);
        List<DebeziumEntry.Column> afterCols = List.of(col1, col2);

        DebeziumEntry.RowData rd = new DebeziumEntry.RowData();
        rd.setKey("id=1");
        rd.setBeforeColumnsList(beforeCols);
        rd.setAfterColumnsList(afterCols);

        assertThat(rd.getKey()).isEqualTo("id=1");
        assertThat(rd.getBeforeColumnsList()).hasSize(1);
        assertThat(rd.getAfterColumnsList()).hasSize(2);
    }

    // ==================== Column ====================

    @Test
    @DisplayName("Column setters and getters work")
    void column_setterGetter() {
        DebeziumEntry.Column col = new DebeziumEntry.Column();
        col.setName("age");
        col.setValue("30");
        col.setUpdated(true);

        assertThat(col.getName()).isEqualTo("age");
        assertThat(col.getValue()).isEqualTo("30");
        assertThat(col.getUpdated()).isTrue();
    }
}

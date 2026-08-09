package io.debezium.embedded.util;

import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RowDataUtil}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("RowDataUtil")
class RowDataUtilTest {

    @Test
    @DisplayName("getBeforeValue returns value for matching column")
    void getBeforeValue_returnsMatchingValue() {
        DebeziumEntry.Column col = new DebeziumEntry.Column();
        col.setName("ID");
        col.setValue("42");
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(List.of(col));

        assertThat(RowDataUtil.getBeforeValue(rowData, "id")).isEqualTo("42");
    }

    @Test
    @DisplayName("getBeforeValue returns null for missing column")
    void getBeforeValue_returnsNull_forMissingColumn() {
        DebeziumEntry.Column col = new DebeziumEntry.Column();
        col.setName("name");
        col.setValue("test");
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(List.of(col));

        assertThat(RowDataUtil.getBeforeValue(rowData, "other")).isNull();
    }

    @Test
    @DisplayName("getBeforeValue returns null for null rowData")
    void getBeforeValue_returnsNull_forNullRowData() {
        assertThat(RowDataUtil.getBeforeValue(null, "col")).isNull();
    }

    @Test
    @DisplayName("getBeforeValue returns null for null beforeColumnsList")
    void getBeforeValue_returnsNull_forNullColumnsList() {
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(null);
        assertThat(RowDataUtil.getBeforeValue(rowData, "col")).isNull();
    }

    @Test
    @DisplayName("getAfterValue returns value for matching column")
    void getAfterValue_returnsMatchingValue() {
        DebeziumEntry.Column col = new DebeziumEntry.Column();
        col.setName("AGE");
        col.setValue("30");
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setAfterColumnsList(List.of(col));

        assertThat(RowDataUtil.getAfterValue(rowData, "age")).isEqualTo("30");
    }

    @Test
    @DisplayName("getAfterValue returns null for null rowData")
    void getAfterValue_returnsNull_forNullRowData() {
        assertThat(RowDataUtil.getAfterValue(null, "col")).isNull();
    }

    @Test
    @DisplayName("getAfterValue returns null for null afterColumnsList")
    void getAfterValue_returnsNull_forNullColumnsList() {
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setAfterColumnsList(null);
        assertThat(RowDataUtil.getAfterValue(rowData, "col")).isNull();
    }

    @Test
    @DisplayName("getValue prefers before over after")
    void getValue_prefersBefore() {
        DebeziumEntry.Column beforeCol = new DebeziumEntry.Column();
        beforeCol.setName("id");
        beforeCol.setValue("before-val");

        DebeziumEntry.Column afterCol = new DebeziumEntry.Column();
        afterCol.setName("id");
        afterCol.setValue("after-val");

        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(List.of(beforeCol));
        rowData.setAfterColumnsList(List.of(afterCol));

        assertThat(RowDataUtil.getValue(rowData, "id")).isEqualTo("before-val");
    }

    @Test
    @DisplayName("getValue falls back to after when before is null")
    void getValue_fallsBackToAfter() {
        DebeziumEntry.Column afterCol = new DebeziumEntry.Column();
        afterCol.setName("id");
        afterCol.setValue("after-val");

        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(null);
        rowData.setAfterColumnsList(List.of(afterCol));

        assertThat(RowDataUtil.getValue(rowData, "id")).isEqualTo("after-val");
    }

    @Test
    @DisplayName("getValue returns null when column not found in either")
    void getValue_returnsNull_whenNotFound() {
        DebeziumEntry.Column col = new DebeziumEntry.Column();
        col.setName("other");
        col.setValue("val");
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(List.of(col));
        rowData.setAfterColumnsList(List.of(col));

        assertThat(RowDataUtil.getValue(rowData, "id")).isNull();
    }

    @Test
    @DisplayName("getBeforeValue with null column value returns null")
    void getBeforeValue_nullValue_returnsNull() {
        DebeziumEntry.Column col = new DebeziumEntry.Column();
        col.setName("id");
        col.setValue(null);
        DebeziumEntry.RowData rowData = new DebeziumEntry.RowData();
        rowData.setBeforeColumnsList(List.of(col));

        assertThat(RowDataUtil.getBeforeValue(rowData, "id")).isNull();
    }
}

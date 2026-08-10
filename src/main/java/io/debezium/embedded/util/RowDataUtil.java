package io.debezium.embedded.util;

import io.debezium.embedded.protocol.DebeziumEntry;

import java.util.List;
import java.util.Objects;

/**
 * Utility methods for reading column values from a {@link DebeziumEntry.RowData}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class RowDataUtil {

    /**
     * Returns the before-state value of the supplied column, case-insensitively.
     *
     * @param rowData    the row data to inspect
     * @param columnName the column name
     * @return the before-state value, or {@code null} when not found
     */
    public static String getBeforeValue(DebeziumEntry.RowData rowData, String columnName) {
        if(Objects.isNull(rowData)){
            return null;
        }
        List<DebeziumEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        if(Objects.isNull(beforeColumnsList)){
            return null;
        }
        for (DebeziumEntry.Column column : beforeColumnsList) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return Objects.toString(column.getValue(), null);
            }
        }
        return null;
    }

    /**
     * Returns the after-state value of the supplied column, case-insensitively.
     *
     * @param rowData    the row data to inspect
     * @param columnName the column name
     * @return the after-state value, or {@code null} when not found
     */
    public static String getAfterValue(DebeziumEntry.RowData rowData, String columnName) {
        if(Objects.isNull(rowData)){
            return null;
        }
        List<DebeziumEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        if(Objects.isNull(afterColumnsList)){
            return null;
        }
        for (DebeziumEntry.Column column : afterColumnsList) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return  Objects.toString(column.getValue(), null);
            }
        }
        return null;
    }

    /**
     * Returns the value of the supplied column, preferring the before-state
     * and falling back to the after-state.
     *
     * @param rowData    the row data to inspect
     * @param columnName the column name
     * @return the resolved value, or {@code null} when not found
     */
    public static String getValue(DebeziumEntry.RowData rowData, String columnName) {
        String value = getBeforeValue(rowData, columnName);
        if(Objects.isNull(value)){
            return getAfterValue(rowData, columnName);
        }
        return value;
    }

}

package io.debezium.embedded.util;

import io.debezium.embedded.protocol.DebeziumEntry;

import java.util.List;
import java.util.Objects;

public class RowDataUtil {

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

    public static String getValue(DebeziumEntry.RowData rowData, String columnName) {
        String value = getBeforeValue(rowData, columnName);
        if(Objects.isNull(value)){
            return getAfterValue(rowData, columnName);
        }
        return value;
    }

}

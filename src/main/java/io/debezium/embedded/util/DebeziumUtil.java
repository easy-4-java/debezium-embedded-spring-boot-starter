package io.debezium.embedded.util;

import com.alibaba.fastjson2.JSON;
import io.debezium.data.Envelope;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.BEFORE;
import static java.util.stream.Collectors.toMap;

/**
 * Utility methods for parsing Debezium change-event records into the starter's
 * domain models.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
/**
 * <p>Auto-configuration for DebeziumUtil.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumUtil {

    /** Result key for the current row data. */
    public static String DATA = "data";
    /** Result key for the previous row data. */
    public static String BEFORE_DATA = "beforeData";
    /** Result key for the event type. */
    public static String EVENT_TYPE = "eventType";
    /** Result key for the table name. */
    public static String TABLE = "table";
    /** JSON key for the change-event payload. */
    public static String PAYLOAD = "payload";

    /**
     * Field names extracted from the Debezium {@code source} struct.
     */
    public enum TableFieldName {

        /** Database instance name. */
        db,
        /** Table name. */
        table,
        /** Change event timestamp (epoch millis). */
        ts_ms
        ;

        /**
         * @param fieldName the candidate field name
         * @return {@code true} if the field is one of the supported {@link TableFieldName} values
         */
        public static Boolean filterJsonField(String fieldName) {
            return Stream.of(values()).map(Enum::name).collect(Collectors.toSet()).contains(fieldName);
        }
    }

    /**
     * Extracts the database/table/timestamp metadata from a change record.
     *
     * @param sourceRecordChangeValue the Debezium change struct
     * @return a map of selected source fields, with {@code timestamp} renamed to {@code changeTime}
     */
    public static Map<String, Object> getChangeTableInfo(Struct sourceRecordChangeValue) {
        Struct struct = (Struct) sourceRecordChangeValue.get(Envelope.FieldName.SOURCE);
        Map<String, Object> map = struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null && TableFieldName.filterJsonField(fieldName))
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
        if (map.containsKey(Envelope.FieldName.TIMESTAMP)) {
            map.put("changeTime", map.get(Envelope.FieldName.TIMESTAMP));
            map.remove(Envelope.FieldName.TIMESTAMP);
        }
        return map;
    }

    /**
     * Builds a {@link DebeziumModel.ChangeListenerModel} from the change record.
     * <p>Only {@code CREATE}, {@code UPDATE} and {@code DELETE} operations are
     * considered; {@code READ} is ignored.</p>
     *
     * @param sourceRecordChangeValue the Debezium change struct
     * @param changeMap               the table metadata returned by {@link #getChangeTableInfo}
     * @return the parsed change-listener model, or {@code null} when not applicable
     */
    public static DebeziumModel.ChangeListenerModel getChangeDataInfo(Struct sourceRecordChangeValue, Map<String, Object> changeMap) {
        // Filter by operation: handle insert/update/delete only
        Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordChangeValue.get(Envelope.FieldName.OPERATION));
        if (operation != Envelope.Operation.READ) {
            Integer eventType = null;
            Map<String, Object> result = new HashMap<>(4);
            if (operation == Envelope.Operation.CREATE) {
                eventType = DebeziumEntry.EventType.CREATE.getIndex();
                result.put(DATA, getChangeData(sourceRecordChangeValue, AFTER));
                result.put(BEFORE_DATA, null);
            }
            // Updates need both before and after state
            if (operation == Envelope.Operation.UPDATE) {
                if (!changeMap.containsKey(TABLE)) {
                    return null;
                }
                eventType = DebeziumEntry.EventType.UPDATE.getIndex();
                String currentTableName = String.valueOf(changeMap.get(TABLE).toString());
                // Ignore changes to non-essential fields
                Map<String, String> resultMap = filterChangeData(sourceRecordChangeValue, currentTableName);
                if (CollectionUtils.isEmpty(resultMap)) {
                    return null;
                }
                result.put(DATA, resultMap.get(AFTER));
                result.put(BEFORE_DATA, resultMap.get(BEFORE));
            }
            if (operation == Envelope.Operation.DELETE) {
                eventType = DebeziumEntry.EventType.DELETE.getIndex();
                result.put(DATA, getChangeData(sourceRecordChangeValue, AFTER));
                result.put(BEFORE_DATA, getChangeData(sourceRecordChangeValue, BEFORE));
            }
            result.put(EVENT_TYPE, eventType);
            result.putAll(changeMap);
        }
        return null;
    }


    /**
     * Filters non-essential change columns and returns the serialised before/after state.
     *
     * @param sourceRecordChangeValue the Debezium change struct
     * @param currentTableName         the table being updated
     * @return a map containing the serialised {@code after} and {@code before} state
     */
    public static Map<String, String> filterChangeData(Struct sourceRecordChangeValue, String currentTableName) {
        Map<String, String> resultMap = new HashMap<>(4);
        Map<String, Object> afterMap = getChangeDataMap(sourceRecordChangeValue, AFTER);
        Map<String, Object> beforeMap = getChangeDataMap(sourceRecordChangeValue, BEFORE);
        //todo filter columns by table
        resultMap.put(AFTER, JSON.toJSONString(afterMap));
        resultMap.put(BEFORE, JSON.toJSONString(beforeMap));
        return resultMap;
    }

    /**
     * Determines whether the change only affects non-essential columns.
     *
     * @param currentTableName  the table being updated
     * @param afterMap          the after state column map
     * @param beforeMap         the before state column map
     * @param filterColumnList  columns to ignore when detecting essential changes
     * @return {@code true} if only non-essential columns changed and the event can be skipped
     */
    public static boolean checkNonEssentialData(String currentTableName, Map<String, Object> afterMap,
                                          Map<String, Object> beforeMap, List<String> filterColumnList) {
        Map<String, Boolean> filterMap = new HashMap<>(16);
        for (String key : afterMap.keySet()) {
            Object afterValue = afterMap.get(key);
            Object beforeValue = beforeMap.get(key);
            filterMap.put(key, !Objects.equals(beforeValue, afterValue));
        }
        filterColumnList.parallelStream().forEach(filterMap::remove);
        if (filterMap.values().stream().noneMatch(x -> x)) {
            log.info("Table {} has no essential data change, skipping this event", currentTableName);
            return true;
        }
        return false;
    }

    /**
     * Serialises the {@code record} ({@code before} or {@code after}) part of the change struct.
     *
     * @param sourceRecordChangeValue the Debezium change struct
     * @param record                  {@link io.debezium.data.Envelope.FieldName#BEFORE} or {@link io.debezium.data.Envelope.FieldName#AFTER}
     * @return the serialised JSON, or {@code null} when empty
     */
    public static String getChangeData(Struct sourceRecordChangeValue, String record) {
        Map<String, Object> changeDataMap = getChangeDataMap(sourceRecordChangeValue, record);
        if (CollectionUtils.isEmpty(changeDataMap)) {
            return null;
        }
        return JSON.toJSONString(changeDataMap);
    }

    /**
     * Materialises the {@code record} ({@code before} or {@code after}) part of the change struct as a map.
     *
     * @param sourceRecordChangeValue the Debezium change struct
     * @param record                  {@link io.debezium.data.Envelope.FieldName#BEFORE} or {@link io.debezium.data.Envelope.FieldName#AFTER}
     * @return a column-name to value map of the changed row
     */
    public static Map<String, Object> getChangeDataMap(Struct sourceRecordChangeValue, String record) {
        Struct struct = (Struct) sourceRecordChangeValue.get(record);
        // Wrap the changed row as a Map
        return struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

}

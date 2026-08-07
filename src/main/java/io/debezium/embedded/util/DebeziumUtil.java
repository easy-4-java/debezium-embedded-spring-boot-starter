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

@Slf4j
public class DebeziumUtil {

    public static String DATA = "data";
    public static String BEFORE_DATA = "beforeData";
    public static String EVENT_TYPE = "eventType";
    public static String TABLE = "table";
    public static String PAYLOAD = "payload";

    public enum TableFieldName {

        /**
         * 数据库实例
         */
        db,
        /**
         * 表名
         */
        table,
        /**
         * 操作时间
         */
        ts_ms
        ;

        public static Boolean filterJsonField(String fieldName) {
            return Stream.of(values()).map(Enum::name).collect(Collectors.toSet()).contains(fieldName);
        }
    }

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

    public static DebeziumModel.ChangeListenerModel getChangeDataInfo(Struct sourceRecordChangeValue, Map<String, Object> changeMap) {
        // 操作类型过滤,只处理增删改
        Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordChangeValue.get(Envelope.FieldName.OPERATION));
        if (operation != Envelope.Operation.READ) {
            Integer eventType = null;
            Map<String, Object> result = new HashMap<>(4);
            if (operation == Envelope.Operation.CREATE) {
                eventType = DebeziumEntry.EventType.CREATE.getIndex();
                result.put(DATA, getChangeData(sourceRecordChangeValue, AFTER));
                result.put(BEFORE_DATA, null);
            }
            // 修改需要特殊处理，拿到前后的数据
            if (operation == Envelope.Operation.UPDATE) {
                if (!changeMap.containsKey(TABLE)) {
                    return null;
                }
                eventType = DebeziumEntry.EventType.UPDATE.getIndex();
                String currentTableName = String.valueOf(changeMap.get(TABLE).toString());
                // 忽略非重要属性变更
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
     * 过滤非重要变更数据
     *
     * @param sourceRecordChangeValue
     * @param currentTableName
     * @return
     */
    public static Map<String, String> filterChangeData(Struct sourceRecordChangeValue, String currentTableName) {
        Map<String, String> resultMap = new HashMap<>(4);
        Map<String, Object> afterMap = getChangeDataMap(sourceRecordChangeValue, AFTER);
        Map<String, Object> beforeMap = getChangeDataMap(sourceRecordChangeValue, BEFORE);
        //todo 根据表过滤字段
        resultMap.put(AFTER, JSON.toJSONString(afterMap));
        resultMap.put(BEFORE, JSON.toJSONString(beforeMap));
        return resultMap;
    }

    /**
     * 校验是否仅仅是非重要字段属性变更
     * @param currentTableName
     * @param afterMap
     * @param beforeMap
     * @param filterColumnList
     * @return
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
            log.info("表：{}无核心资料变更，忽略此次操作!", currentTableName);
            return true;
        }
        return false;
    }

    public static String getChangeData(Struct sourceRecordChangeValue, String record) {
        Map<String, Object> changeDataMap = getChangeDataMap(sourceRecordChangeValue, record);
        if (CollectionUtils.isEmpty(changeDataMap)) {
            return null;
        }
        return JSON.toJSONString(changeDataMap);
    }

    public static Map<String, Object> getChangeDataMap(Struct sourceRecordChangeValue, String record) {
        Struct struct = (Struct) sourceRecordChangeValue.get(record);
        // 将变更的行封装为Map
        return struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

}

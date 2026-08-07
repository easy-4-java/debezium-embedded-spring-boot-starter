package io.debezium.embedded.util;


import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.annotation.DebeziumTable;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.enums.TableNameEnum;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 处理器工具类
 */
public class HandlerUtil {

    protected static Map<String, Predicate<DebeziumEventHolder>> eventPredicateMap = new ConcurrentHashMap<>();

    public static RecordChangeEventEntryHandler getEntryHandler(List<? extends RecordChangeEventEntryHandler> entryHandlers, String schemaName, String tableName) {
        StringJoiner joiner = new StringJoiner(".").add(schemaName).add(tableName);
        RecordChangeEventEntryHandler globalHandler = null;
        for (RecordChangeEventEntryHandler handler : entryHandlers) {
            String debeziumTableNameCombination = getDebeziumTableNameCombination(handler);
            if (StringUtils.isBlank(debeziumTableNameCombination)) {
                continue;
            }
            if (TableNameEnum.ALL.name().toLowerCase().equals(debeziumTableNameCombination)) {
                globalHandler = handler;
                continue;
            }
            if (debeziumTableNameCombination.equals(joiner.toString().toLowerCase())) {
                return handler;
            }
            String name = GenericUtil.getTableGenericProperties(handler);
            if (name != null) {
                if (name.equals(tableName)) {
                    return handler;
                }
            }
        }
        return globalHandler;
    }


    public static Map<String, RecordChangeEventEntryHandler> getTableHandlerMap(List<? extends RecordChangeEventEntryHandler> entryHandlers) {
        Map<String, RecordChangeEventEntryHandler> map = new ConcurrentHashMap<>();
        if (CollectionUtils.isEmpty(entryHandlers)) {
            return map;
        }
        for (RecordChangeEventEntryHandler handler : entryHandlers) {
            String debeziumTableNameCombination = getDebeziumTableNameCombination(handler);
            if (StringUtils.isNotBlank(debeziumTableNameCombination)) {
                map.putIfAbsent(debeziumTableNameCombination.toLowerCase(), handler);
            } else {
                String name = GenericUtil.getTableGenericProperties(handler);
                if (name != null) {
                    map.putIfAbsent(name.toLowerCase(), handler);
                }
            }
        }
        return map;
    }

    /**
     * 获取事件处理器Map, 此方法会将事件处理器按照 destination,schema,table,eventType 的拼接值进行分组
     * @param eventHolders
     * @return
     */
    public static Map<String, List<DebeziumEventHolder>> getEventHolderMap(List<DebeziumEventHolder> eventHolders) {
        Map<String, List<DebeziumEventHolder>> map = new ConcurrentHashMap<>();
        if (CollectionUtils.isEmpty(eventHolders)) {
            return map;
        }
        for (DebeziumEventHolder holder : eventHolders) {
            List<String> debeziumTableNameCombinations = getDebeziumTableNameCombinations(holder);
            if (CollectionUtils.isEmpty(debeziumTableNameCombinations)) {
                continue;
            }
            for (String debeziumTableNameCombination : debeziumTableNameCombinations) {
                map.computeIfAbsent(debeziumTableNameCombination, k -> new ArrayList<>()).add(holder);
            }
        }
        return map;
    }

    public static List<DebeziumEventHolder> getEventHolders(Map<String, List<DebeziumEventHolder>> map,
                                                        String destination,
                                                        String schemaName,
                                                        String tableName,
                                                        DebeziumEntry.EventType eventType) {
        // 获取四个属性的拼接值
        String key = getCombinationValue(destination, schemaName, tableName, eventType);
        // 获取唯一值对应的过滤器
        Predicate<DebeziumEventHolder> predicate =  eventPredicateMap.computeIfAbsent(key, k -> getAnnotationFilter(destination, schemaName, tableName, eventType));
        // 返回过滤后的结果
        return map.getOrDefault(key, Collections.emptyList()).stream().filter(predicate).collect(Collectors.toList());
    }

    public static RecordChangeEventEntryHandler getEntryHandler(Map<String, RecordChangeEventEntryHandler> map, String schemaName, String tableName) {
        StringJoiner joiner = new StringJoiner(".").add(schemaName).add(tableName);
        RecordChangeEventEntryHandler entryHandler = map.get(joiner.toString().toLowerCase());
        if (entryHandler == null) {
            return map.get(TableNameEnum.ALL.name().toLowerCase());
        }
        return entryHandler;
    }

    /**
     * 获取注解过滤器
     * @param destination debezium 指令
     * @param schemaName 数据库实例
     * @param tableName 表名
     * @param eventType 事件类型
     * @return 过滤器
     */
    protected static Predicate<DebeziumEventHolder> getAnnotationFilter(String destination,
                                                                     String schemaName,
                                                                     String tableName,
                                                                     DebeziumEntry.EventType eventType) {

        // 比较 destination 是否一致，如果没有指定 destination 则默认为所有
        Predicate<DebeziumEventHolder> df = holder -> StringUtils.isEmpty(holder.getEvent().destination())
                || holder.getEvent().destination().equals(destination) || destination == null;

        // 比较数据库实例名是否一致
        Predicate<DebeziumEventHolder> sf = holder -> StringUtils.isNotBlank(holder.getEvent().schema())
                && holder.getEvent().schema().equalsIgnoreCase(schemaName);

        // 比较表名是否一致，如果没有指定表名则默认为所有
        Predicate<DebeziumEventHolder> tf = holder -> StringUtils.isNotBlank(holder.getEvent().table())
                && ( holder.getEvent().table().equalsIgnoreCase(tableName) || holder.getEvent().table().equals(TableNameEnum.ALL.getTable()) );

        // 比较事件类型是否一致
        Predicate<DebeziumEventHolder> ef = holder -> holder.getEvent().eventType().length > 0 && Arrays.stream(holder.getEvent().eventType()).anyMatch(ev -> ev == eventType) ;

        return df.and(sf).and(tf).and(ef);
    }

    public static String getDebeziumTableNameCombination(RecordChangeEventEntryHandler entryHandler) {
        DebeziumTable debeziumTable = entryHandler.getClass().getAnnotation(DebeziumTable.class);
        if (Objects.nonNull(debeziumTable)) {
            return getCombinationValue(debeziumTable.destination(), debeziumTable.schema(), debeziumTable.table());
        }
        return null;
    }

    public static List<String> getDebeziumTableNameCombinations(DebeziumEventHolder eventHolder) {
        OnDebeziumEvent debeziumEvent = eventHolder.getEvent();
        if (Objects.nonNull(debeziumEvent) && Objects.nonNull(debeziumEvent.eventType()) && debeziumEvent.eventType().length > 0) {
            return Arrays.stream(debeziumEvent.eventType())
                    .map(eventType -> getCombinationValue(debeziumEvent.destination(), debeziumEvent.schema(), debeziumEvent.table(), eventType))
                    .distinct().collect(Collectors.toList());
        }
        return null;
    }

    public static String getCombinationValue(String destination, String schema, String table) {
        destination = StringUtils.defaultIfBlank(destination, TableNameEnum.ALL.getDestination());
        schema = StringUtils.defaultIfBlank(schema, TableNameEnum.ALL.getSchema());
        table = StringUtils.defaultIfBlank(table, TableNameEnum.ALL.getTable());
        StringJoiner joiner = new StringJoiner(TableNameEnum.DELIMITER).add(destination).add(schema).add(table);
        return joiner.toString().toLowerCase();
    }

    public static String getCombinationValue(String destination, String schema, String table, DebeziumEntry.EventType eventType) {
        destination = StringUtils.defaultIfBlank(destination, TableNameEnum.ALL.getDestination());
        schema = StringUtils.defaultIfBlank(schema, TableNameEnum.ALL.getSchema());
        table = StringUtils.defaultIfBlank(table, TableNameEnum.ALL.getTable());
        StringJoiner joiner = new StringJoiner(TableNameEnum.DELIMITER).add(destination).add(schema).add(table).add(eventType.name().toLowerCase());
        return joiner.toString().toLowerCase();
    }

}

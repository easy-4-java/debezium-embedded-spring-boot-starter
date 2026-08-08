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
 * Utilities for indexing and resolving change-event handlers.
 * <p>Handles the registration, lookup by {@code schema.table} combination,
 * and the annotation based filtering used by the annotation dispatch path.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class HandlerUtil {

    protected static Map<String, Predicate<DebeziumEventHolder>> eventPredicateMap = new ConcurrentHashMap<>();

    /**
     * Resolves the most specific entry handler for the supplied schema/table.
     * <p>A handler bound to {@link TableNameEnum#ALL} is returned as a fallback
     * only when no more specific handler matches.</p>
     *
     * @param entryHandlers the candidate handlers
     * @param schemaName    the database schema name
     * @param tableName     the table name
     * @return the matching handler, or the wildcard handler, or {@code null}
     */
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


    /**
     * Builds a lookup map of programmatic handlers keyed by their resolved
     * {@code schema.table} combination (lower-cased).
     *
     * @param entryHandlers the handlers to index
     * @return a non-null, possibly empty, map
     */
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
     * Indexes event holders by their {@code destination.schema.table.eventType} combination.
     *
     * @param eventHolders the holders to index
     * @return a map keyed by the combination value
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

    /**
     * Resolves the annotation based event holders matching the supplied criteria.
     *
     * @param map         the indexed holder map (built by {@link #getEventHolderMap})
     * @param destination the connector destination
     * @param schemaName  the database schema name
     * @param tableName   the table name
     * @param eventType   the event type
     * @return the matching holders, possibly empty but never {@code null}
     */
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

    /**
     * Looks up an entry handler by {@code schema.table} from a pre-built map,
     * falling back to the wildcard handler when no exact match exists.
     *
     * @param map        the handler map (built by {@link #getTableHandlerMap})
     * @param schemaName the database schema name
     * @param tableName  the table name
     * @return the matching handler, or the wildcard handler, or {@code null}
     */
    public static RecordChangeEventEntryHandler getEntryHandler(Map<String, RecordChangeEventEntryHandler> map, String schemaName, String tableName) {
        StringJoiner joiner = new StringJoiner(".").add(schemaName).add(tableName);
        RecordChangeEventEntryHandler entryHandler = map.get(joiner.toString().toLowerCase());
        if (entryHandler == null) {
            return map.get(TableNameEnum.ALL.name().toLowerCase());
        }
        return entryHandler;
    }

    /**
     * Builds the composite predicate used to match an annotation based holder.
     *
     * @param destination the connector destination, or {@code null} for any
     * @param schemaName  the database schema name
     * @param tableName   the table name
     * @param eventType   the event type
     * @return a predicate matching the supplied criteria
     */
    protected static Predicate<DebeziumEventHolder> getAnnotationFilter(String destination,
                                                                     String schemaName,
                                                                     String tableName,
                                                                     DebeziumEntry.EventType eventType) {

        // Match destination; empty annotation destination means any
        Predicate<DebeziumEventHolder> df = holder -> StringUtils.isEmpty(holder.getEvent().destination())
                || holder.getEvent().destination().equals(destination) || destination == null;

        // Match schema name
        Predicate<DebeziumEventHolder> sf = holder -> StringUtils.isNotBlank(holder.getEvent().schema())
                && holder.getEvent().schema().equalsIgnoreCase(schemaName);

        // Match table name; wildcard means any
        Predicate<DebeziumEventHolder> tf = holder -> StringUtils.isNotBlank(holder.getEvent().table())
                && ( holder.getEvent().table().equalsIgnoreCase(tableName) || holder.getEvent().table().equals(TableNameEnum.ALL.getTable()) );

        // Match event type
        Predicate<DebeziumEventHolder> ef = holder -> holder.getEvent().eventType().length > 0 && Arrays.stream(holder.getEvent().eventType()).anyMatch(ev -> ev == eventType) ;

        return df.and(sf).and(tf).and(ef);
    }

    /**
     * Resolves the {@code destination.schema.table} combination declared via
     * the {@link DebeziumTable} annotation on the handler.
     *
     * @param entryHandler the handler to inspect
     * @return the combination value, or {@code null} when the handler is not annotated
     */
    public static String getDebeziumTableNameCombination(RecordChangeEventEntryHandler entryHandler) {
        DebeziumTable debeziumTable = entryHandler.getClass().getAnnotation(DebeziumTable.class);
        if (Objects.nonNull(debeziumTable)) {
            return getCombinationValue(debeziumTable.destination(), debeziumTable.schema(), debeziumTable.table());
        }
        return null;
    }

    /**
     * Resolves all {@code destination.schema.table.eventType} combinations
     * declared by the supplied event holder.
     *
     * @param eventHolder the holder to inspect
     * @return the distinct combination values, or {@code null} when none declared
     */
    public static List<String> getDebeziumTableNameCombinations(DebeziumEventHolder eventHolder) {
        OnDebeziumEvent debeziumEvent = eventHolder.getEvent();
        if (Objects.nonNull(debeziumEvent) && Objects.nonNull(debeziumEvent.eventType()) && debeziumEvent.eventType().length > 0) {
            return Arrays.stream(debeziumEvent.eventType())
                    .map(eventType -> getCombinationValue(debeziumEvent.destination(), debeziumEvent.schema(), debeziumEvent.table(), eventType))
                    .distinct().collect(Collectors.toList());
        }
        return null;
    }

    /**
     * Builds a {@code destination.schema.table} combination value, substituting
     * the {@link TableNameEnum#ALL} wildcard for blank components.
     *
     * @param destination the connector destination
     * @param schema      the schema name
     * @param table       the table name
     * @return the lower-cased combination value
     */
    public static String getCombinationValue(String destination, String schema, String table) {
        destination = StringUtils.defaultIfBlank(destination, TableNameEnum.ALL.getDestination());
        schema = StringUtils.defaultIfBlank(schema, TableNameEnum.ALL.getSchema());
        table = StringUtils.defaultIfBlank(table, TableNameEnum.ALL.getTable());
        StringJoiner joiner = new StringJoiner(TableNameEnum.DELIMITER).add(destination).add(schema).add(table);
        return joiner.toString().toLowerCase();
    }

    /**
     * Builds a {@code destination.schema.table.eventType} combination value,
     * substituting the {@link TableNameEnum#ALL} wildcard for blank components.
     *
     * @param destination the connector destination
     * @param schema      the schema name
     * @param table       the table name
     * @param eventType   the event type
     * @return the lower-cased combination value
     */
    public static String getCombinationValue(String destination, String schema, String table, DebeziumEntry.EventType eventType) {
        destination = StringUtils.defaultIfBlank(destination, TableNameEnum.ALL.getDestination());
        schema = StringUtils.defaultIfBlank(schema, TableNameEnum.ALL.getSchema());
        table = StringUtils.defaultIfBlank(table, TableNameEnum.ALL.getTable());
        StringJoiner joiner = new StringJoiner(TableNameEnum.DELIMITER).add(destination).add(schema).add(table).add(eventType.name().toLowerCase());
        return joiner.toString().toLowerCase();
    }

}

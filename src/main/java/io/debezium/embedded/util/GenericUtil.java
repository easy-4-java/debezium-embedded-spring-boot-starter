package io.debezium.embedded.util;


import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflection utilities for resolving generic types and assembling invocation
 * arguments for annotation based event handlers.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class GenericUtil {

    private static Map<Class<? extends RecordChangeEventEntryHandler>, Class> cache = new ConcurrentHashMap<>();

    /**
     * Builds the argument array for an annotation based handler method, binding
     * the model, row-change and event-type to matching parameters.
     *
     * @param method     the handler method to bind for
     * @param model      the current change model
     * @param rowChange  the row change payload
     * @param eventType  the event type
     * @return the argument array in declaration order
     */
    public static Object[] getInvokeArgs(Method method, DebeziumModel model, DebeziumEntry.RowChange rowChange, DebeziumEntry.EventType eventType) {
        return Arrays.stream(method.getParameterTypes()).map(pClass -> {
                    if(DebeziumModel.class.isAssignableFrom(pClass)){
                        return model;
                    }
                    if(DebeziumEntry.RowChange.class.isAssignableFrom(pClass)) {
                        return rowChange;
                    }
                    if(DebeziumEntry.EventType.class.isAssignableFrom(pClass)) {
                        return eventType;
                    }
                    return null;
                })
                .toArray();
    }

    /**
     * Builds the argument array for an annotation based handler method, binding
     * the model, row data list and event-type to matching parameters.
     *
     * @param method     the handler method to bind for
     * @param model      the current change model
     * @param rowData    the row data payload as a list of column maps
     * @param eventType  the event type
     * @return the argument array in declaration order
     */
    public static Object[] getInvokeArgs(Method method, DebeziumModel model, List<Map<String, String>> rowData, DebeziumEntry.EventType eventType) {
        return Arrays.stream(method.getParameterTypes()).map(pClass -> {
                if(DebeziumModel.class.isAssignableFrom(pClass)){
                    return model;
                }
                if(List.class.isAssignableFrom(pClass)) {
                    return rowData;
                }
                if(DebeziumEntry.EventType.class.isAssignableFrom(pClass)) {
                    return eventType;
                }
                return null;
            }).toArray();
    }

    /**
     * Resolves the MyBatis-Plus table name for the handler's generic row type.
     *
     * @param entryHandler the handler to inspect
     * @return the resolved table name, or {@code null} when unavailable
     */
    public static String getTableGenericProperties(RecordChangeEventEntryHandler entryHandler) {
        Class<?> tableClass = getTableClass(entryHandler);
        if (tableClass != null) {
            // Read MyBatis-Plus table metadata
            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
            if (Objects.nonNull(tableInfo)) {
                return tableInfo.getTableName();
            }
        }
        return null;
    }


    /**
     * Resolves the concrete row model type declared by a handler.
     *
     * @param object the handler instance
     * @param <T>    the row model type
     * @return the resolved {@link Class}, or {@code null} when not parameterised
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTableClass(RecordChangeEventEntryHandler object) {
        // Resolve the handler's generic type argument
        Class<? extends RecordChangeEventEntryHandler> handlerClass = object.getClass();
        Class tableClass = cache.get(handlerClass);
        if (tableClass == null) {
            Type[] interfacesTypes = handlerClass.getGenericInterfaces();
            for (Type t : interfacesTypes) {
                Class c = (Class) ((ParameterizedType) t).getRawType();
                if (c.equals(RecordChangeEventEntryHandler.class)) {
                    tableClass = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
                    cache.putIfAbsent(handlerClass, tableClass);
                    return tableClass;
                }
            }
        }
        return tableClass;
    }


}

package io.debezium.embedded.factory;


import io.debezium.embedded.enums.TableNameEnum;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;

public abstract class AbstractModelFactory<T> implements IModelFactory<T> {

    @Override
    public <R> R newInstance(RecordChangeEventEntryHandler entryHandler, T t) throws Exception {
        String debeziumTableName = HandlerUtil.getDebeziumTableNameCombination(entryHandler);
        if (TableNameEnum.ALL.name().toLowerCase().equals(debeziumTableName)) {
            return (R) t;
        }
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass != null) {
            return newInstance(tableClass, t);
        }
        return null;
    }

    abstract <R> R newInstance(Class<R> tableClass, T t) throws Exception;
}

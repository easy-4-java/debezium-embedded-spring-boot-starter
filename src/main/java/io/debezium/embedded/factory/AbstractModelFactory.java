package io.debezium.embedded.factory;


import io.debezium.embedded.enums.TableNameEnum;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;

/**
 * Base {@link IModelFactory} implementation that resolves the target row type
 * from the handler's generic signature and delegates to a subclass for the
 * concrete instantiation.
 * <p>Handlers bound to the wildcard {@link TableNameEnum#ALL} table receive
 * the raw payload unchanged.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public abstract class AbstractModelFactory<T> implements IModelFactory<T> {

    /** {@inheritDoc} */
    @Override
    /**
     * <p>New instance.</p>
     * @param entryHandler
     * @param t
     * @return the result
     */
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

    /**
     * Instantiates a new row model of the supplied type from the raw payload.
     *
     * @param tableClass the target row type
     * @param t          the raw payload
     * @param <R>        the row model type
     * @return the materialised row model
     * @throws Exception if instantiation fails
     */
    abstract <R> R newInstance(Class<R> tableClass, T t) throws Exception;
}

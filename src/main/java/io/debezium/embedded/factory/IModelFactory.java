package io.debezium.embedded.factory;


import io.debezium.embedded.handler.RecordChangeEventEntryHandler;

import java.util.Set;

/**
 * Factory that materialises a row model of type {@code R} from a raw payload
 * of type {@code T}, using the metadata declared on the supplied
 * {@link RecordChangeEventEntryHandler}.
 *
 * @param <T> the raw payload type (e.g. list of columns, map of values)
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface IModelFactory<T> {


    /**
     * Creates a new row model instance from the supplied payload.
     *
     * @param entryHandler the handler carrying the target type metadata
     * @param t            the raw payload
     * @param <R>          the row model type
     * @return the materialised row model
     * @throws Exception if instantiation fails
     */
    <R> R newInstance(RecordChangeEventEntryHandler entryHandler, T t) throws Exception;

    /**
     * Creates a new row model instance restricted to the supplied updated columns.
     * <p>Default implementation returns {@code null}; override to support
     * partial updates.</p>
     *
     * @param entryHandler the handler carrying the target type metadata
     * @param t            the raw payload
     * @param updateColumn the set of updated column names
     * @param <R>          the row model type
     * @return the materialised row model, or {@code null} if not supported
     * @throws Exception if instantiation fails
     */
    default <R> R newInstance(RecordChangeEventEntryHandler entryHandler, T t, Set<String> updateColumn) throws Exception {
        return null;
    }
}

package io.debezium.embedded.handler.impl;


import io.debezium.embedded.factory.IModelFactory;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.protocol.DebeziumEntry;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link RowDataHandler} that materialises rows from a list of column maps.
 * <p>For {@code UPDATE} events the list is expected to contain
 * {@code [after, before]}; for {@code CREATE} and {@code DELETE} a single
 * element is expected.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class MapRowDataHandlerImpl implements RowDataHandler<List<Map<String, String>>> {

    /** Factory used to convert the raw maps into the handler's row model. */
    private IModelFactory<Map<String,String>> modelFactory;

    /**
     * Creates a new handler using the supplied model factory.
     *
     * @param modelFactory factory used to materialise rows
     */
    public MapRowDataHandlerImpl(IModelFactory<Map<String, String>> modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Dispatches the supplied row payload to the matching callback.
     *
     * @param list          the row payload ({@code [after]} for create/delete, {@code [after, before]} for update)
     * @param entryHandler  the callback to invoke
     * @param eventType     the change event type
     * @param <R>           the row model type
     * @throws Exception if materialisation or dispatch fails
     */
    @Override
    /**
     * <p>Handler row data.</p>
     * @param list
     * @param entryHandler
     * @param eventType
     * @return the result
     */
    public <R> void handlerRowData(List<Map<String, String>> list, RecordChangeEventEntryHandler<R> entryHandler, DebeziumEntry.EventType eventType) throws Exception{
        if (Objects.isNull(list) || Objects.isNull(entryHandler) || Objects.isNull(eventType)) {
            return;
        }
        switch (eventType) {
            case CREATE:
                R entry  = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.insert(entry);
                break;
            case UPDATE:
                R before = modelFactory.newInstance(entryHandler, list.get(1));
                R after = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.update(before, after);
                break;
            case DELETE:
                R o = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.delete(o);
                break;
            default:
                break;
        }
    }
}

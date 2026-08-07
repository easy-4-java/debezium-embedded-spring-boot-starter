package io.debezium.embedded.factory;


import io.debezium.embedded.handler.RecordChangeEventEntryHandler;

import java.util.Set;

public interface IModelFactory<T> {


    <R> R newInstance(RecordChangeEventEntryHandler entryHandler, T t) throws Exception;

    default <R> R newInstance(RecordChangeEventEntryHandler entryHandler, T t, Set<String> updateColumn) throws Exception {
        return null;
    }
}

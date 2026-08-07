package io.debezium.embedded.client;

import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.experimental.Accessors;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Accessors(chain = true)
public abstract class AbstractClientBuilder<D extends DebeziumClient> {

    /**
     * Change Event Handler
     */
    protected ChangeEventHandler changeEventHandler;
    /**
     * Record Change Event Handler
     */
    protected RecordChangeEventHandler recordChangeEventHandler;

    protected List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines;
    protected List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines;
    protected ThreadPoolTaskExecutor debeziumTaskExecutor;

    public AbstractClientBuilder<D> changeEventHandler(ChangeEventHandler changeEventHandler) {
        this.changeEventHandler = changeEventHandler;
        return this;
    }

    public AbstractClientBuilder<D> recordChangeEventHandler(RecordChangeEventHandler recordChangeEventHandler) {
        this.recordChangeEventHandler = recordChangeEventHandler;
        return this;
    }

    public AbstractClientBuilder<D> changeEventEngines(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines) {
        this.changeEventEngines = changeEventEngines;
        return this;
    }

    public AbstractClientBuilder<D> recordChangeEventEngines(List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines) {
        this.recordChangeEventEngines = recordChangeEventEngines;
        return this;
    }

    public AbstractClientBuilder<D> debeziumTaskExecutor(ThreadPoolTaskExecutor debeziumTaskExecutor) {
        this.debeziumTaskExecutor = debeziumTaskExecutor;
        return this;
    }

    public abstract D build();

}

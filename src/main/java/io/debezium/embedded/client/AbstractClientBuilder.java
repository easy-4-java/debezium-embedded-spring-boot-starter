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

/**
 * Base builder for {@link DebeziumClient} implementations.
 * <p>Uses Lombok's {@link Accessors} to provide a fluent, chainable API for
 * configuring the handlers, engines and executor before {@link #build()}.</p>
 *
 * @param <D> the concrete client type produced by this builder
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Accessors(chain = true)
public abstract class AbstractClientBuilder<D extends DebeziumClient> {

    /** Handler for JSON change events. */
    protected ChangeEventHandler changeEventHandler;
    /** Handler for record change events. */
    protected RecordChangeEventHandler recordChangeEventHandler;

    /** Engines emitting JSON {@link ChangeEvent}s. */
    protected List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines;
    /** Engines emitting {@link RecordChangeEvent}s. */
    protected List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines;
    /** Executor used to run the engines. */
    protected ThreadPoolTaskExecutor debeziumTaskExecutor;

    /**
     * Sets the change-event handler.
     *
     * @param changeEventHandler handler for JSON change events
     * @return this builder for chaining
     */
    public AbstractClientBuilder<D> changeEventHandler(ChangeEventHandler changeEventHandler) {
        this.changeEventHandler = changeEventHandler;
        return this;
    }

    /**
     * Sets the record-change-event handler.
     *
     * @param recordChangeEventHandler handler for record change events
     * @return this builder for chaining
     */
    public AbstractClientBuilder<D> recordChangeEventHandler(RecordChangeEventHandler recordChangeEventHandler) {
        this.recordChangeEventHandler = recordChangeEventHandler;
        return this;
    }

    /**
     * Sets the JSON change-event engines.
     *
     * @param changeEventEngines engines producing JSON change events
     * @return this builder for chaining
     */
    public AbstractClientBuilder<D> changeEventEngines(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines) {
        this.changeEventEngines = changeEventEngines;
        return this;
    }

    /**
     * Sets the record change-event engines.
     *
     * @param recordChangeEventEngines engines producing record change events
     * @return this builder for chaining
     */
    public AbstractClientBuilder<D> recordChangeEventEngines(List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines) {
        this.recordChangeEventEngines = recordChangeEventEngines;
        return this;
    }

    /**
     * Sets the executor used to run the engines.
     *
     * @param debeziumTaskExecutor task executor
     * @return this builder for chaining
     */
    public AbstractClientBuilder<D> debeziumTaskExecutor(ThreadPoolTaskExecutor debeziumTaskExecutor) {
        this.debeziumTaskExecutor = debeziumTaskExecutor;
        return this;
    }

    /**
     * Builds the client instance from the configured components.
     *
     * @return a new client of type {@code D}
     */
    public abstract D build();

}

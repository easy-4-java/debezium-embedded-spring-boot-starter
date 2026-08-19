package io.debezium.embedded.client;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * Default {@link DebeziumClient} implementation backed by the Debezium embedded engine.
 * <p>
 * Supports multiple engine instances configured through the auto-configuration
 * and consolidates the responsibilities previously held by
 * {@code DebeziumEmbeddedRunner}.
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
/**
 * <p>Auto-configuration for DebeziumEmbeddedClient.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumEmbeddedClient extends AbstractDebeziumClient<RecordChangeEvent<SourceRecord>> {

    /**
     * Creates a new client from the supplied engines and executor.
     *
     * @param changeEventEngines       engines producing JSON change events
     * @param recordChangeEventEngines engines producing record change events
     * @param executor                 executor used to run the engines
     */
    private DebeziumEmbeddedClient(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines,
                                   List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines,
                                   ThreadPoolTaskExecutor executor) {
        super(changeEventEngines, recordChangeEventEngines, executor);
    }

    /**
     * Fluent builder for constructing {@link DebeziumEmbeddedClient} instances.
     */
    public static final class Builder extends AbstractClientBuilder<DebeziumEmbeddedClient> {

        /**
         * Builds the {@link DebeziumEmbeddedClient} from the configured engines and executor.
         *
         * @return a new client instance
         */
        @Override
        /**
         * <p>Build.</p>
         * @return the result
         */
        public DebeziumEmbeddedClient build() {
            return new DebeziumEmbeddedClient(changeEventEngines, recordChangeEventEngines, debeziumTaskExecutor);
        }
    }
}

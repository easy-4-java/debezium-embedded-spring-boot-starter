package io.debezium.embedded.spring.boot;

import lombok.Data;

/**
 * Tuning properties for the Debezium asynchronous embedded engine.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@Data
public class DebeziumAsyncEngineProperties {

    /**
     * Number of worker threads available to process change-event records.
     * <p>
     * When left at the default, the engine uses a Java {@code ThreadPoolExecutor}
     * that scales dynamically with the workload, capped at the available CPU cores.
     * When an explicit value is set, the engine uses a fixed thread pool of that
     * size. Use the placeholder {@code AVAILABLE_CORES} to request all cores.
     * </p>
     */
    private int threads = Runtime.getRuntime().availableProcessors();

    /** Maximum time (ms) to wait for already-committed records to finish processing during shutdown (default {@code 1000}). */
    private long shutdownTimeoutMs = 1000;

    /** Processing order of records within the engine (default {@link Order#ORDERED}). */
    private Order order = Order.ORDERED;

    /**
     * Whether the engine should build a default {@code ChangeConsumer} from the
     * supplied consumer so that processing happens serially. Has no effect when
     * the engine is created with a custom {@code ChangeConsumer}.
     */
    private boolean withSerialConsumer = false;

    /** Time (ms) the engine waits for task lifecycle operations (start/stop) to complete (default {@code 180000}). */
    private long timeoutMs = 180000;

    /**
     * Record processing ordering strategy.
     * <p>
     * {@link #UNORDERED} processing delivers higher throughput because records
     * are emitted as soon as SMT processing and serialisation complete, without
     * waiting for other records. The ordering option has no effect when a custom
     * {@code ChangeConsumer} is supplied to the engine.
     * </p>
     */
    public enum Order {

        /** Records are processed and emitted in the same order they were captured from the source database. */
        ORDERED,
        /** Records may be emitted out of order relative to the source database. */
        UNORDERED;
    }
}

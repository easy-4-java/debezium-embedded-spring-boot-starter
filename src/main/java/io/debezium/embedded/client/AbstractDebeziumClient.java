package io.debezium.embedded.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.debezium.config.Configuration;
import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.*;

/**
 * Base implementation of {@link DebeziumClient} that owns one or more
 * {@link DebeziumEngine} instances and runs them on a dedicated executor.
 * <p>
 * Subclasses customise how individual change events and record-change events
 * are processed. Lifecycle methods delegate to the underlying engines and
 * executor.
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractDebeziumClient<R> implements InitializingBean, DebeziumClient {

    /** Whether the client is currently running. */
    protected volatile boolean running;
    /** Engines emitting JSON {@link ChangeEvent}s. */
    private List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines;
    /** Engines emitting {@link RecordChangeEvent}s. */
    private List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines;

    /** Handler for JSON change events. */
    private ChangeEventHandler changeEventHandler;
    /** Handler for record change events. */
    private RecordChangeEventHandler recordChangeEventHandler;
    /** Factory used to create worker threads. */
    protected ThreadFactory threadFactory;
    /** Executor used to run the engines. */
    protected ThreadPoolTaskExecutor executor;

    /**
     * Creates a new client bound to the given engines and executor.
     *
     * @param changeEventEngines       engines producing JSON change events
     * @param recordChangeEventEngines engines producing record change events
     * @param executor                 executor used to run the engines
     */
    public AbstractDebeziumClient(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines,
                                  List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines,
                                  ThreadPoolTaskExecutor executor) {
        this.changeEventEngines = changeEventEngines;
        this.recordChangeEventEngines = recordChangeEventEngines;
        this.executor = executor;
    }

    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet() {

    }

    /**
     * Starts every change-event engine on the executor and marks the client as running.
     */
    @Override
    public void start() {
        log.info("Start Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        for (DebeziumEngine<ChangeEvent<String, String>> debeziumEngine : changeEventEngines) {
            executor.execute(debeziumEngine);
        }
        this.running = true;
    }

    /**
     * Stops the client, closing each engine and finally shutting the executor down.
     */
    @SneakyThrows
    @Override
    public void stop() {
        log.info("Stop Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        this.running = false;
        for (DebeziumEngine<ChangeEvent<String, String>> debeziumEngine : changeEventEngines) {
            try {
                log.info("Stop Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
                debeziumEngine.close();
                log.info("Stopped Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Error stopping Debezium Engine Of Instance： {}", this.getClass().getSimpleName(), e);
            }
        }
        for (DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine : recordChangeEventEngines) {
            try {
                log.info("Stop Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
                debeziumEngine.close();
                log.info("Stopped Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Error stopping Debezium Engine Of Instance： {}", this.getClass().getSimpleName(), e);
            }
        }
        Thread.sleep(2000);
        log.warn(ThreadPoolEnum.SQL_SERVER_LISTENER_POOL + " thread pool shutting down!");
        executor.shutdown();
    }

    /** @return {@code true} when the client is running. */
    @Override
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Resolves the logical destination name for a JSON change event.
     *
     * @param configuration the engine configuration
     * @param changeEvent   the change event
     * @return the destination name
     */
    protected String getDestination(Configuration configuration, ChangeEvent<String, String> changeEvent){
        return changeEvent.destination();
    }

    /**
     * Resolves the logical destination name for a record change event.
     *
     * @param configuration      the engine configuration
     * @param recordChangeEvent  the record change event
     * @return the destination name read from the configuration
     */
    protected String getDestination(Configuration configuration, RecordChangeEvent<SourceRecord> recordChangeEvent){
        return configuration.getString("destination");
    }


    /** {@inheritDoc} */
    @Override
    public void process(ChangeEvent<String, String> changeEvent) {

    }

    /** {@inheritDoc} */
    @Override
    public void process(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                        DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter) {

    }



    /**
     * Holder for the auxiliary {@code sql-server-listener-pool} thread pool.
     * Implemented as an enum to guarantee a thread-safe singleton.
     */
    public enum ThreadPoolEnum {

        /** Singleton instance. */
        INSTANCE;

        /** Name of the auxiliary thread pool. */
        public static final String SQL_SERVER_LISTENER_POOL = "sql-server-listener-pool";
        /** Backing executor service. */
        private final ExecutorService es;


        /**
         * Creates the backing thread pool (private; enum initialisation).
         */
        ThreadPoolEnum() {
            final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(SQL_SERVER_LISTENER_POOL + "-%d").build();
            es = new ThreadPoolExecutor(8, 16, 60,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(256),
                    threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        }


        /**
         * @return the singleton {@link ExecutorService}.
         */
        public ExecutorService getInstance() {
            return es;
        }
    }

}

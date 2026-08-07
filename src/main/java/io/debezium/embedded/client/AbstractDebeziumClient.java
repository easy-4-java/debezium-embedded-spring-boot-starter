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
 * Debezium Client 抽象类
 */
@Slf4j
public abstract class AbstractDebeziumClient<R> implements InitializingBean, DebeziumClient {

    /**
     * 是否运行中
     */
    protected volatile boolean running;
    /**
     * Debezium Engine
     */
    private List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines;
    private List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines;

    /**
     * 消息处理器
     */
    private ChangeEventHandler changeEventHandler;
    private RecordChangeEventHandler recordChangeEventHandler;
    /**
     * 线程工厂
     */
    protected ThreadFactory threadFactory;
    /**
     * 线程池
     */
    protected ThreadPoolTaskExecutor executor;

    public AbstractDebeziumClient(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines,
                                  List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines,
                                  ThreadPoolTaskExecutor executor) {
        this.changeEventEngines = changeEventEngines;
        this.recordChangeEventEngines = recordChangeEventEngines;
        this.executor = executor;
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void start() {
        log.info("Start Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        for (DebeziumEngine<ChangeEvent<String, String>> debeziumEngine : changeEventEngines) {
            executor.execute(debeziumEngine);
        }
        this.running = true;
    }

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
        log.warn(ThreadPoolEnum.SQL_SERVER_LISTENER_POOL + "线程池关闭!");
        executor.shutdown();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    protected String getDestination(Configuration configuration, ChangeEvent<String, String> changeEvent){
        return changeEvent.destination();
    }

    protected String getDestination(Configuration configuration, RecordChangeEvent<SourceRecord> recordChangeEvent){
        return configuration.getString("destination");
    }


    @Override
    public void process(ChangeEvent<String, String> changeEvent) {

    }

    @Override
    public void process(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                        DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter) {

    }



    public enum ThreadPoolEnum {

        /**
         * 实例
         */
        INSTANCE;

        public static final String SQL_SERVER_LISTENER_POOL = "sql-server-listener-pool";
        /**
         * 线程池单例
         */
        private final ExecutorService es;


        /**
         * 枚举 (构造器默认为私有）
         */
        ThreadPoolEnum() {
            final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(SQL_SERVER_LISTENER_POOL + "-%d").build();
            es = new ThreadPoolExecutor(8, 16, 60,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(256),
                    threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        }


        /**
         * 公有方法
         *
         * @return ExecutorService
         */
        public ExecutorService getInstance() {
            return es;
        }
    }

}

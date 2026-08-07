package io.debezium.embedded.spring.boot;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.embedded.client.DebeziumEmbeddedClient;
import io.debezium.embedded.configurer.connector.ConnectorConfigurer;
import io.debezium.embedded.configurer.connector.ConnectorConfigurerFactory;
import io.debezium.embedded.configurer.history.SchemaHistoryConfigurer;
import io.debezium.embedded.configurer.history.SchemaHistoryConfigurerFactory;
import io.debezium.embedded.configurer.storage.OffsetStorageConfigurer;
import io.debezium.embedded.configurer.storage.OffsetStorageConfigurerFactory;
import io.debezium.embedded.factory.MapColumnModelFactory;
import io.debezium.embedded.handler.*;
import io.debezium.embedded.handler.impl.MapRowDataHandlerImpl;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.debezium.engine.format.Json;
import io.debezium.engine.spi.OffsetCommitPolicy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Debezium Embedded 自动配置
 * 支持多个数据库实例的 Embedded 模式，支持多种数据库连接器
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ DebeziumEngine.class })
@EnableConfigurationProperties({DebeziumEmbeddedProperties.class, DebeziumThreadPoolProperties.class})
@Import(DebeziumThreadPoolAutoConfiguration.class)
@Slf4j
public class DebeziumEmbeddedAutoConfiguration {

    /**
     * Default completion callback which just logs the error. If connector finishes successfully it does nothing.
     */
    @Slf4j
    public static class DefaultCompletionCallback implements DebeziumEngine.CompletionCallback {
        @Override
        public void handle(final boolean success, final String message, final Throwable error) {
            if (!success) {
                log.error(message, error);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public DebeziumEngine.CompletionCallback completionCallback() {
        return new DefaultCompletionCallback();
    }

    @Bean
    @ConditionalOnMissingBean
    public RowDataHandler<List<Map<String, String>>> recordRowDataHandler() {
        return new MapRowDataHandlerImpl(new MapColumnModelFactory());
    }

    @Bean
    @ConditionalOnMissingBean
    public ChangeEventHandler changeEventHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                 ObjectProvider<RecordChangeEventEntryHandler> entryHandlerProvider) {
        return new DefaultChangeEventHandler(entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public RecordChangeEventHandler recordChangeEventHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                ObjectProvider<RecordChangeEventEntryHandler> entryHandlerProvider) {
        return new DefaultRecordChangeEventHandler(entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler);
    }

    /**
     * 初始化 DebeziumEmbeddedClient
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public DebeziumEmbeddedClient singleDebeziumEmbeddedClient(DebeziumEmbeddedProperties properties,
                                                               ObjectProvider<Clock> clockProvider,
                                                               ObjectProvider<DebeziumEngine.CompletionCallback> completionCallbackProvider,
                                                               ObjectProvider<DebeziumEngine.ConnectorCallback> connectorCallbackProvider,
                                                               ObjectProvider<OffsetCommitPolicy> offsetCommitPolicyProvider,
                                                               ObjectProvider<ChangeEventHandler> changeEventHandlerProvider,
                                                               ObjectProvider<RecordChangeEventHandler> recordChangeEventHandlerProvider,
                                                               @Qualifier("debeziumEmbeddedExecutor") ThreadPoolTaskExecutor debeziumTaskExecutor) {

        ChangeEventHandler changeEventHandler = changeEventHandlerProvider.getIfAvailable();
        RecordChangeEventHandler recordChangeEventHandler = recordChangeEventHandlerProvider.getIfAvailable();
        if (Objects.isNull(changeEventHandler) && Objects.isNull(recordChangeEventHandler)) {
            throw new IllegalStateException("No change event handler configured");
        }
        if (CollectionUtils.isEmpty(properties.getInstances())) {
            throw new IllegalStateException("No instances configured for single instance mode");
        }

        Clock clock = clockProvider.getIfAvailable(Clock::systemDefaultZone);
        DebeziumEngine.CompletionCallback completionCallback = completionCallbackProvider.getIfAvailable();
        DebeziumEngine.ConnectorCallback connectorCallback = connectorCallbackProvider.getIfAvailable();

        List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines = new ArrayList<>();
        List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines = new ArrayList<>();
        for (DebeziumEmbeddedProperties.Instance instance : properties.getInstances()) {

            log.info("Creating DebeziumEmbeddedClient instance for destination: {}", instance.getConnector().getDestination());

            String instanceName = instance.getConnector().getDestination();
            if (instanceName == null || instanceName.trim().isEmpty()) {
                instanceName = "debezium-instance-" + System.currentTimeMillis();
                log.warn("Instance has no destination name, using generated name: {}", instanceName);
            } else {
                log.warn("Instance has no destination name, using connector's destination: {}", instanceName);
            }

            DebeziumAsyncEngineProperties asyncEngineProperties = instance.getAsync();
            DebeziumConnectorProperties connectorProperties = instance.getConnector();
            DebeziumOffsetStorageProperties storageProperties = instance.getOffsetStorage();
            DebeziumSchemaHistoryProperties historyProperties = instance.getSchemaHistory();

            // 1. 创建基础配置
            Configuration.Builder builder = Configuration.create()
                    .with("name", connectorProperties.getDestination());

            if (Objects.nonNull(asyncEngineProperties)) {
                builder.with("record.processing.threads", asyncEngineProperties.getThreads())
                        .with("record.processing.shutdown.timeout.ms", asyncEngineProperties.getShutdownTimeoutMs())
                        .with("record.processing.order", asyncEngineProperties.getOrder().name())
                        .with("record.processing.with.serial.consumer", asyncEngineProperties.isWithSerialConsumer())
                        .with("task.management.timeout.ms", asyncEngineProperties.getTimeoutMs());
            }

            // 2. 交由连接器配置器写入数据库相关配置
            ConnectorConfigurer connectorConfigurer = ConnectorConfigurerFactory.from(connectorProperties);
            connectorConfigurer.apply(builder, connectorProperties);

            // 3. 交由存储配置器写入 offset 相关参数
            OffsetStorageConfigurer storageConfigurer = OffsetStorageConfigurerFactory.from(storageProperties);
            storageConfigurer.apply(builder, storageProperties);

            // 4. 交由历史配置器写入数据库历史配置
            SchemaHistoryConfigurer historyConfigurer = SchemaHistoryConfigurerFactory.from(historyProperties);
            historyConfigurer.apply(builder, historyProperties);

            Configuration config = builder.build();

            // 4. 创建 DebeziumEngine
            if (DebeziumEmbeddedProperties.EventType.CHANGE_EVENT.equals(instance.getEventType()) && Objects.nonNull(changeEventHandler)) {
                DebeziumEngine.Builder<ChangeEvent<String, String>> engineBuilder = DebeziumEngine
                        .create(Json.class)
                        .using(config.asProperties())
                        .using(clock)
                        .using(offsetCommitPolicyProvider.getIfAvailable(OffsetCommitPolicy::always))
                        .notifying(event -> changeEventHandler.handleEvent(event, config.asProperties()));
                if (Objects.nonNull(completionCallback)) {
                    engineBuilder.using(completionCallback);
                }
                if (Objects.nonNull(connectorCallback)) {
                    engineBuilder.using(connectorCallback);
                }
                changeEventEngines.add(engineBuilder.build());
            } else if (DebeziumEmbeddedProperties.EventType.RECORD_CHANGE_EVENT.equals(instance.getEventType()) && Objects.nonNull(recordChangeEventHandler)) {
                DebeziumEngine.Builder<RecordChangeEvent<SourceRecord>> engineBuilder = DebeziumEngine
                        .create(ChangeEventFormat.of(Connect.class))
                        .using(config.asProperties())
                        .using(clock)
                        .using(offsetCommitPolicyProvider.getIfAvailable(OffsetCommitPolicy::always))
                        .notifying((events, committer) -> recordChangeEventHandler.handleEvent(events, committer, config.asProperties()));
                if (Objects.nonNull(completionCallback)) {
                    engineBuilder.using(completionCallback);
                }
                if (Objects.nonNull(connectorCallback)) {
                    engineBuilder.using(connectorCallback);
                }
                log.info("Build Debezium Engine For Instance: {}", config.asProperties().getProperty("connector.class"));
                recordChangeEventEngines.add(engineBuilder.build());
            }
        }
        return new DebeziumEmbeddedClient.Builder()
                    .changeEventEngines(changeEventEngines)
                    .changeEventHandler(changeEventHandler)
                    .recordChangeEventEngines(recordChangeEventEngines)
                    .recordChangeEventHandler(recordChangeEventHandler)
                    .debeziumTaskExecutor(debeziumTaskExecutor)
                    .build();
    }
}



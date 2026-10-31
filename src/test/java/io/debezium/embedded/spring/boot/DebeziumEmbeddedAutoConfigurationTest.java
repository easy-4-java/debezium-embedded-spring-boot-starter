package io.debezium.embedded.spring.boot;

import io.debezium.embedded.client.DebeziumEmbeddedClient;
import io.debezium.embedded.configurer.storage.OffsetStorageType;
import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.spi.OffsetCommitPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DebeziumEmbeddedAutoConfigurationTest {
    private final DebeziumEmbeddedAutoConfiguration configuration = new DebeziumEmbeddedAutoConfiguration();

    @Test
    void createsDefaultHandlerBeans() {
        assertInstanceOf(DebeziumEmbeddedAutoConfiguration.DefaultCompletionCallback.class,
                configuration.completionCallback());
        configuration.completionCallback().handle(true, "done", null);
        configuration.completionCallback().handle(false, "failed", new IllegalStateException("sample"));
        assertInstanceOf(io.debezium.embedded.handler.impl.MapRowDataHandlerImpl.class,
                configuration.recordRowDataHandler());
    }

    @Test
    void rejectsMissingHandlersBeforeInspectingInstances() {
        assertThrows(IllegalStateException.class, () -> createClient(new DebeziumEmbeddedProperties(), null, null));
    }

    @Test
    void rejectsMissingInstancesWithAConfiguredHandler() {
        assertThrows(IllegalStateException.class, () -> createClient(new DebeziumEmbeddedProperties(),
                mock(ChangeEventHandler.class), null));
    }

    @Test
    void buildsJsonEngineWithoutStartingIt() {
        DebeziumEmbeddedProperties properties = configuredInstance(DebeziumEmbeddedProperties.EventType.CHANGE_EVENT);
        DebeziumEmbeddedClient client = createClient(properties, mock(ChangeEventHandler.class), null);
        assertFalse(client.isRunning());
    }

    @Test
    void buildsRecordEngineWithoutStartingIt() {
        DebeziumEmbeddedProperties properties = configuredInstance(DebeziumEmbeddedProperties.EventType.RECORD_CHANGE_EVENT);
        DebeziumEmbeddedClient client = createClient(properties, null, mock(RecordChangeEventHandler.class));
        assertFalse(client.isRunning());
    }

    private static DebeziumEmbeddedProperties configuredInstance(DebeziumEmbeddedProperties.EventType eventType) {
        DebeziumEmbeddedProperties properties = new DebeziumEmbeddedProperties();
        DebeziumEmbeddedProperties.Instance instance = new DebeziumEmbeddedProperties.Instance();
        instance.setEventType(eventType);
        instance.getConnector().setDestination("test-connector");
        instance.getConnector().setHost("localhost");
        instance.getConnector().setPort(3306);
        instance.getConnector().setUsername("test");
        instance.getConnector().setPassword("test");
        instance.getConnector().setServerId("1");
        instance.getOffsetStorage().setType(OffsetStorageType.MEMORY);
        properties.setInstances(List.of(instance));
        return properties;
    }

    @SuppressWarnings("unchecked")
    private DebeziumEmbeddedClient createClient(DebeziumEmbeddedProperties properties,
                                                ChangeEventHandler changeHandler,
                                                RecordChangeEventHandler recordHandler) {
        ObjectProvider<Clock> clocks = mock(ObjectProvider.class);
        when(clocks.getIfAvailable(any())).thenReturn(Clock.systemUTC());
        ObjectProvider<DebeziumEngine.CompletionCallback> completions = mock(ObjectProvider.class);
        ObjectProvider<DebeziumEngine.ConnectorCallback> connectors = mock(ObjectProvider.class);
        ObjectProvider<OffsetCommitPolicy> offsetPolicies = mock(ObjectProvider.class);
        when(offsetPolicies.getIfAvailable(any())).thenReturn(OffsetCommitPolicy.always());
        ObjectProvider<ChangeEventHandler> changeHandlers = mock(ObjectProvider.class);
        when(changeHandlers.getIfAvailable()).thenReturn(changeHandler);
        ObjectProvider<RecordChangeEventHandler> recordHandlers = mock(ObjectProvider.class);
        when(recordHandlers.getIfAvailable()).thenReturn(recordHandler);

        return configuration.singleDebeziumEmbeddedClient(properties, clocks, completions,
                connectors, offsetPolicies, changeHandlers, recordHandlers, mock(ThreadPoolTaskExecutor.class));
    }
}

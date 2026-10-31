package io.debezium.embedded.client;

import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AbstractDebeziumClientTest {

    @Test
    void startsAndStopsEnginesUsingTheConfiguredExecutor() throws Exception {
        @SuppressWarnings("unchecked")
        DebeziumEngine<ChangeEvent<String, String>> changeEngine = mock(DebeziumEngine.class);
        @SuppressWarnings("unchecked")
        DebeziumEngine<RecordChangeEvent<SourceRecord>> recordEngine = mock(DebeziumEngine.class);
        ThreadPoolTaskExecutor executor = mock(ThreadPoolTaskExecutor.class);
        TestClient client = new TestClient(List.of(changeEngine), List.of(recordEngine), executor);

        assertFalse(client.isRunning());
        client.afterPropertiesSet();
        client.start();
        assertTrue(client.isRunning());
        verify(executor).execute(changeEngine);

        client.stop();
        assertFalse(client.isRunning());
        verify(changeEngine).close();
        verify(recordEngine).close();
        verify(executor).shutdown();
    }

    @Test
    void resolvesDestinationsFromTheMatchingEventSource() {
        TestClient client = new TestClient(List.of(), List.of(), mock(ThreadPoolTaskExecutor.class));
        @SuppressWarnings("unchecked")
        ChangeEvent<String, String> changeEvent = mock(ChangeEvent.class);
        org.mockito.Mockito.when(changeEvent.destination()).thenReturn("orders");
        Configuration configuration = Configuration.create().with("destination", "inventory").build();

        assertEquals("orders", client.changeDestination(configuration, changeEvent));
        assertEquals("inventory", client.recordDestination(configuration, mock(RecordChangeEvent.class)));
    }

    private static class TestClient extends AbstractDebeziumClient<Object> {
        TestClient(List<DebeziumEngine<ChangeEvent<String, String>>> changeEngines,
                   List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordEngines,
                   ThreadPoolTaskExecutor executor) {
            super(changeEngines, recordEngines, executor);
        }

        String changeDestination(Configuration configuration, ChangeEvent<String, String> event) {
            return getDestination(configuration, event);
        }

        String recordDestination(Configuration configuration, RecordChangeEvent<SourceRecord> event) {
            return getDestination(configuration, event);
        }
    }
}

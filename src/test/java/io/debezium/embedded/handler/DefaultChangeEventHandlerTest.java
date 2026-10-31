package io.debezium.embedded.handler;

import io.debezium.engine.ChangeEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import io.debezium.embedded.annotation.DebeziumEventHandler;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Mockito.*;

class DefaultChangeEventHandlerTest {
    @SuppressWarnings("unchecked")
    private final RowDataHandler<List<Map<String, String>>> rows = mock(RowDataHandler.class);
    private final DefaultChangeEventHandler handler = new DefaultChangeEventHandler(List.of(), rows);

    @Test
    void handlesTombstoneAndMissingPayloadWithoutDispatch() {
        ChangeEvent<String, String> event = mock(ChangeEvent.class);
        handler.handleEvent(event, new Properties());
        when(event.value()).thenReturn("{}");
        handler.handleEvent(event, new Properties());
        verifyNoInteractions(rows);
    }

    @Test
    void parsesValidEnvelopeWithoutThrowingOnUnknownOperation() {
        ChangeEvent<String, String> event = mock(ChangeEvent.class);
        when(event.value()).thenReturn("{\"payload\":{\"op\":\"c\",\"source\":{\"db\":\"app\",\"table\":\"users\"},\"after\":{\"id\":\"7\"}}}");
        handler.handleEvent(event, new Properties());
    }

    @Test
    void discoversAnnotatedHandlerBeans() {
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(DebeziumEventHandler.class)).thenReturn(Map.of("listener", new Listener()));
        handler.setApplicationContext(context);
    }

    @DebeziumEventHandler
    static class Listener { void onChange() {} }
}

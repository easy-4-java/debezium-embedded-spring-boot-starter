package io.debezium.embedded.annotation;

import io.debezium.embedded.annotation.event.OnInsertEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DebeziumEventHolder}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumEventHolder")
class DebeziumEventHolderTest {

    private OnDebeziumEvent resolveEvent(Method method) {
        return AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
    }

    @Test
    @DisplayName("isMatch returns true when event type matches")
    void isMatch_returnsTrue_whenEventTypeMatches() throws Exception {
        Method method = SampleHandler.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = resolveEvent(method);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleHandler(), method, event);

        assertThat(holder.isMatch(DebeziumEntry.EventType.CREATE)).isTrue();
    }

    @Test
    @DisplayName("isMatch returns false when event type does not match")
    void isMatch_returnsFalse_whenEventTypeDoesNotMatch() throws Exception {
        Method method = SampleHandler.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = resolveEvent(method);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleHandler(), method, event);

        assertThat(holder.isMatch(DebeziumEntry.EventType.DELETE)).isFalse();
    }

    @Test
    @DisplayName("isMatch returns true when eventType is null")
    void isMatch_returnsTrue_whenEventTypeNull() throws Exception {
        Method method = SampleHandler.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = resolveEvent(method);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleHandler(), method, event);

        assertThat(holder.isMatch(null)).isTrue();
    }

    @Test
    @DisplayName("isMatch returns correct results for each event type")
    void isMatch_returnsCorrectForEachType() throws Exception {
        Method method = SampleHandler.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = resolveEvent(method);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleHandler(), method, event);

        // @OnInsertEvent has eventType = CREATE, so it only matches CREATE
        assertThat(holder.isMatch(DebeziumEntry.EventType.CREATE)).isTrue();
        assertThat(holder.isMatch(DebeziumEntry.EventType.UPDATE)).isFalse();
        assertThat(holder.isMatch(DebeziumEntry.EventType.DELETE)).isFalse();
        assertThat(holder.isMatch(DebeziumEntry.EventType.TRUNCATE)).isFalse();
    }

    @Test
    @DisplayName("getters return correct values")
    void getters_returnCorrectValues() throws Exception {
        Method method = SampleHandler.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = resolveEvent(method);
        SampleHandler target = new SampleHandler();
        DebeziumEventHolder holder = new DebeziumEventHolder(target, method, event);

        assertThat(holder.getTarget()).isSameAs(target);
        assertThat(holder.getMethod()).isSameAs(method);
        assertThat(holder.getEvent()).isNotNull();
    }

    // ==================== Test fixture ====================

    static class SampleHandler {
        @OnInsertEvent(schema = "public", table = "users")
        public void onCreate() {}
    }
}

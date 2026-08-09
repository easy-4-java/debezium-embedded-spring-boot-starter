package io.debezium.embedded.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link DebeziumThreadUncaughtExceptionHandler}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumThreadUncaughtExceptionHandler")
class DebeziumThreadUncaughtExceptionHandlerTest {

    @Test
    @DisplayName("uncaughtException logs without throwing")
    void uncaughtException_doesNotThrow() {
        DebeziumThreadUncaughtExceptionHandler handler = new DebeziumThreadUncaughtExceptionHandler();
        Thread testThread = new Thread(() -> {}, "test-thread");
        RuntimeException exception = new RuntimeException("test error");

        assertThatCode(() -> handler.uncaughtException(testThread, exception))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("uncaughtException handles null exception gracefully")
    void uncaughtException_handlesNullException() {
        DebeziumThreadUncaughtExceptionHandler handler = new DebeziumThreadUncaughtExceptionHandler();
        Thread testThread = new Thread(() -> {}, "test-thread");

        assertThatCode(() -> handler.uncaughtException(testThread, null))
            .doesNotThrowAnyException();
    }
}

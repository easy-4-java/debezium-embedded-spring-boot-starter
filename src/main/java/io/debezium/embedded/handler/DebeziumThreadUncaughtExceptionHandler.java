package io.debezium.embedded.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link Thread.UncaughtExceptionHandler} used by the Debezium worker threads.
 * <p>Logs uncaught exceptions so they are not silently swallowed by the
 * executor.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
/**
 * <p>Auto-configuration for DebeziumThreadUncaughtExceptionHandler.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Logs the uncaught throwable along with the offending thread name.
     *
     * @param t the thread that threw
     * @param e the uncaught throwable
     */
    @Override
    /**
     * <p>Uncaught exception.</p>
     * @param t
     * @param e
     */
    public void uncaughtException(Thread t, Throwable e) {
        log.error("thread "+ t.getName()+" have a exception",e);
    }

}

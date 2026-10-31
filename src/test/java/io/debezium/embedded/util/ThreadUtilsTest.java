package io.debezium.embedded.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ThreadUtils}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("ThreadUtils")
class ThreadUtilsTest {

    @Test
    @DisplayName("newThreadPoolExecutor returns a working executor")
    void newThreadPoolExecutor_returnsWorkingExecutor() {
        ExecutorService executor = ThreadUtils.newThreadPoolExecutor(1, 2, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10), "test-pool", true);
        assertThat(executor).isNotNull();
        AtomicBoolean executed = new AtomicBoolean(false);
        executor.submit(() -> executed.set(true));
        ThreadUtils.shutdownGracefully(executor, 5, TimeUnit.SECONDS);
        assertThat(executed.get()).isTrue();
    }

    @Test
    @DisplayName("newSingleThreadExecutor returns a working executor")
    void newSingleThreadExecutor_returnsWorkingExecutor() {
        ExecutorService executor = ThreadUtils.newSingleThreadExecutor("test-single", true);
        assertThat(executor).isNotNull();
        AtomicBoolean executed = new AtomicBoolean(false);
        executor.submit(() -> executed.set(true));
        ThreadUtils.shutdownGracefully(executor, 5, TimeUnit.SECONDS);
        assertThat(executed.get()).isTrue();
    }

    @Test
    @DisplayName("newSingleThreadScheduledExecutor returns a working executor")
    void newSingleThreadScheduledExecutor_returnsWorkingExecutor() {
        ScheduledExecutorService executor = ThreadUtils.newSingleThreadScheduledExecutor("test-sched", true);
        assertThat(executor).isNotNull();
        ThreadUtils.shutdownGracefully(executor, 5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("newFixedThreadScheduledPool returns a working executor")
    void newFixedThreadScheduledPool_returnsWorkingExecutor() {
        ScheduledExecutorService executor = ThreadUtils.newFixedThreadScheduledPool(2, "test-fixed", true);
        assertThat(executor).isNotNull();
        ThreadUtils.shutdownGracefully(executor, 5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("newThreadFactory creates threads with correct prefix")
    void newThreadFactory_createsThreadsWithPrefix() {
        ThreadFactory factory = ThreadUtils.newThreadFactory("myprocess", true);
        Thread thread = factory.newThread(() -> {});
        assertThat(thread.getName()).startsWith("Remoting-myprocess_");
        assertThat(thread.isDaemon()).isTrue();
    }

    @Test
    @DisplayName("newGenericThreadFactory creates threads with correct name")
    void newGenericThreadFactory_createsThreadsWithName() {
        ThreadFactory factory = ThreadUtils.newGenericThreadFactory("generic", false);
        Thread thread = factory.newThread(() -> {});
        assertThat(thread.getName()).startsWith("generic_");
        assertThat(thread.isDaemon()).isFalse();
    }

    @Test
    @DisplayName("newGenericThreadFactory with threads param creates correct naming")
    void newGenericThreadFactory_withThreadsParam() {
        ThreadFactory factory = ThreadUtils.newGenericThreadFactory("pool", 4, true);
        Thread thread = factory.newThread(() -> {});
        assertThat(thread.getName()).startsWith("pool_4_");
        assertThat(thread.isDaemon()).isTrue();
    }

    @Test
    @DisplayName("newThread creates thread with correct properties")
    void newThread_createsCorrectThread() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Thread thread = ThreadUtils.newThread("mythread", () -> executed.set(true), true);
        assertThat(thread.getName()).isEqualTo("mythread");
        assertThat(thread.isDaemon()).isTrue();
        assertThat(thread.getUncaughtExceptionHandler()).isNotNull();
        thread.start();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertThat(executed.get()).isTrue();
    }

    @Test
    @DisplayName("shutdownGracefully with null thread does not throw")
    void shutdownGracefully_nullThread_doesNotThrow() {
        ThreadUtils.shutdownGracefully((Thread) null);
        ThreadUtils.shutdownGracefully((Thread) null, 0);
    }

    @Test
    @DisplayName("shutdownGracefully with executor terminates properly")
    void shutdownGracefully_executor_terminates() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicBoolean executed = new AtomicBoolean(false);
        executor.submit(() -> executed.set(true));
        ThreadUtils.shutdownGracefully(executor, 5, TimeUnit.SECONDS);
        assertThat(executor.isShutdown()).isTrue();
    }

    @Test
    @DisplayName("shutdownGracefully with timed thread join works")
    void shutdownGracefully_timedThread() {
        Thread thread = new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) { /* expected */ }
        });
        thread.start();
        ThreadUtils.shutdownGracefully(thread, 1000);
        assertThat(thread.isAlive()).isFalse();
    }

    @Test
    @DisplayName("newGenericThreadFactory default creates non-daemon threads")
    void newGenericThreadFactory_default_createsNonDaemon() {
        ThreadFactory factory = ThreadUtils.newGenericThreadFactory("default");
        Thread thread = factory.newThread(() -> {});
        assertThat(thread.getName()).startsWith("default_");
        assertThat(thread.isDaemon()).isFalse();
    }

    @Test
    @DisplayName("newThreadFactory with daemon=false creates non-daemon threads")
    void newThreadFactory_nonDaemon() {
        ThreadFactory factory = ThreadUtils.newThreadFactory("myproc", false);
        Thread thread = factory.newThread(() -> {});
        assertThat(thread.getName()).startsWith("Remoting-myproc_");
        assertThat(thread.isDaemon()).isFalse();
    }
}

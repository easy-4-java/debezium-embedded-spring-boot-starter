/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.debezium.embedded.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

/**
 * Thread-pool configuration for the Debezium embedded engine executor.
 * <p>Bound to the {@code debezium.thread-pool.*} namespace.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ConfigurationProperties(DebeziumThreadPoolProperties.PREFIX)
@Data
/**
 * <p>Auto-configuration for DebeziumThreadPoolProperties.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumThreadPoolProperties {

	/** Configuration prefix used by Spring Boot to bind properties. */
	public static final String PREFIX = "debezium.thread-pool";

	/** Core thread pool size (default {@code 1}). Must be positive. */
	private int corePoolSize = 1;

	/** Maximum thread pool size (default is the JVM's available processor count). */
	private int maxPoolSize = Runtime.getRuntime().availableProcessors();

	/** Capacity of the executor's blocking queue (default {@code Integer.MAX_VALUE}); positive values use a {@code LinkedBlockingQueue}, others use a {@code SynchronousQueue}. */
	private int queueCapacity = Integer.MAX_VALUE;

	/** Thread keep-alive duration (default 60 seconds). */
	private Duration keepAlive = Duration.ofSeconds(60);

	/**
	 * Whether core threads are allowed to time out, enabling dynamic growing
	 * and shrinking even with a non-zero queue (the max pool size only grows
	 * once the queue is full). Defaults to {@code false}.
	 */
	private boolean allowCoreThreadTimeOut = false;

	/** Whether to wait for queued tasks to complete on shutdown (default {@code false}). */
	private boolean waitForTasksToCompleteOnShutdown = false;

	/** Maximum seconds to wait for task termination on shutdown (default {@code 0}). */
	private int awaitTerminationSeconds = 0;

	/** Prefix used to name newly created threads (default {@code RedisAsyncTaskExecutor-}). */
	private String threadNamePrefix = "RedisAsyncTaskExecutor-";

	/**
	 * Whether the factory should create daemon threads that only live as long
	 * as the application. Default {@code false}: concrete factories usually
	 * support explicit cancellation, so by default Runnables finish execution
	 * on application shutdown. Set {@code true} for eager shutdown.
	 */
	private boolean daemon = false;

	/**
	 * Rejected-execution policy for the executor. Defaults to the JDK's
	 * {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy abort policy}.
	 */
	private RejectedPolicy rejectedPolicy = RejectedPolicy.AbortPolicy;


	/**
	 * Rejected-execution policies.
	 * <ul>
	 *   <li>{@code CallerRunsPolicy} - run the task on the caller thread (e.g. main thread)</li>
	 *   <li>{@code AbortPolicy} - throw a {@code RejectedExecutionException}</li>
	 *   <li>{@code DiscardPolicy} - silently discard the new task</li>
	 *   <li>{@code DiscardOldestPolicy} - drop the oldest queued task</li>
	 * </ul>
	 */
	public enum RejectedPolicy {

		AbortPolicy((e) -> new ThreadPoolExecutor.AbortPolicy()),
		CallerRunsPolicy((e) -> new ThreadPoolExecutor.CallerRunsPolicy()),
		DiscardPolicy((e) -> new ThreadPoolExecutor.DiscardPolicy()),
		DiscardOldestPolicy((e) -> new ThreadPoolExecutor.DiscardOldestPolicy());

		private final Function<Object, RejectedExecutionHandler> function;

		RejectedPolicy(Function<Object, RejectedExecutionHandler> function) {
			this.function = function;
		}

		/**
		 * Builds the {@link RejectedExecutionHandler} associated with this policy.
		 *
		 * @return the handler instance
		 */
		public RejectedExecutionHandler getRejectedExecutionHandler(){
			return this.function.apply(null);
		}

	}
}

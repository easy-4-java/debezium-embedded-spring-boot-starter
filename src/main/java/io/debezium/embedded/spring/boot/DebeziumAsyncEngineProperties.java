package io.debezium.embedded.spring.boot;

import lombok.Data;

/**
 * Debezium 异步引擎属性
 */
@Data
public class DebeziumAsyncEngineProperties {

    /**
     * 可用于处理变更事件记录的线程数量。 如果未指定值（默认情况），
     * 引擎将使用 Java ThreadPoolExecutor 根据当前工作负载动态调整线程数量。
     * 最大线程数为给定机器上的 CPU 核心数。
     * 如果指定了值，引擎将使用 Java 固定线程池 方法创建具有指定线程数量的线程池。
     * 要使用给定机器上所有可用的核心，请设置占位符值 AVAILABLE_CORES。
     */
    private int threads = Runtime.getRuntime().availableProcessors();

    /**
     * 任务关闭后等待处理已提交记录的最大时间（以毫秒为单位）。
     */
    private long shutdownTimeoutMs = 1000;

    /**
     * 数据库端口
     */
    private Order order = Order.ORDERED;

    /**
     * 指定是否应从提供的 Consumer 创建默认的 ChangeConsumer，从而实现串行 Consumer 处理。如果在使用 API 创建引擎时指定了 ChangeConsumer 接口，则此选项无效。
     */
    private boolean withSerialConsumer = false;

    /**
     * 引擎等待任务生命周期管理操作（启动和停止）完成的时间，以毫秒为单位。
     */
    private long timeoutMs = 180000;

    /**
     * <pre>
     * * UNORDERED 选项的非顺序处理能带来更高的吞吐量，
     * * 因为记录在完成任何 SMT 处理及消息序列化后即刻生成，无需等待其他记录。当引擎提供了 ChangeConsumer 方法时，此选项不会产生任何效果。
     * </pre>
     */
    public enum Order {

        /**
         *  记录按顺序处理；即按照从数据库中获取的顺序进行生成。
         */
        ORDERED,
        /**
         *  记录以非顺序方式处理；即，它们的生成顺序可能与源数据库中的顺序不同。
         */
        UNORDERED;
    }
}

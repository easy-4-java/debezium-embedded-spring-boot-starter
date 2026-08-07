package io.debezium.embedded.configurer.storage;

/**
 * Offset 存储类型枚举。
 */
public enum OffsetStorageType {

    /**
     * 文件存储
     */
    MEMORY,
    /**
     * 文件存储
     */
    FILE,
    /**
     * Kafka 存储
     */
    KAFKA,
    
    /**
     * JDBC 存储
     */
    JDBC,
    
    /**
     * Redis 存储
     */
    REDIS,
    
    /**
     * 自定义存储
     */
    CUSTOM
}



package io.debezium.embedded.configurer.history;

/**
 * 数据库历史记录类型枚举。
 */
public enum SchemaHistoryType {

    /**
     * 文件历史记录
     */
    FILE,
    
    /**
     * Kafka 历史记录
     */
    KAFKA,
    
    /**
     * JDBC 历史记录
     */
    JDBC,
    
    /**
     * Redis 历史记录
     */
    REDIS,
    
    /**
     * S3 历史记录
     */
    S3,
    
    /**
     * RocketMQ 历史记录
     */
    ROCKETMQ,
    
    /**
     * Azure Blob Storage 历史记录
     */
    AZURE_BLOB,
    
    /**
     * 自定义历史记录
     */
    CUSTOM
}

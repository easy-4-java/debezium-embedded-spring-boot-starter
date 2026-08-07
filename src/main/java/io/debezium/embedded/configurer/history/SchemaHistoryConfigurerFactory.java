package io.debezium.embedded.configurer.history;

import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * 数据库历史记录配置器工厂。
 */
public class SchemaHistoryConfigurerFactory {
    
    /**
     * 根据历史记录类型创建对应的配置器。
     *
     * @param historyProperties 历史记录类型
     * @return 历史记录配置器
     */
    public static SchemaHistoryConfigurer from(DebeziumSchemaHistoryProperties historyProperties) {
        switch (historyProperties.getType()) {
            case FILE:
                return new FileSchemaHistoryConfigurer();
            case KAFKA:
                return new KafkaSchemaHistoryConfigurer();
            case JDBC:
                return new JdbcSchemaHistoryConfigurer();
            case REDIS:
                return new RedisSchemaHistoryConfigurer();
            case S3:
                return new AmazonS3SchemaHistoryConfigurer();
            case ROCKETMQ:
                return new RocketMqSchemaHistoryConfigurer();
            case AZURE_BLOB:
                return new AzureBlobSchemaHistoryConfigurer();
            case CUSTOM:
                return new CustomSchemaHistoryConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported schema history type: " + historyProperties.getType());
        }
    }
}

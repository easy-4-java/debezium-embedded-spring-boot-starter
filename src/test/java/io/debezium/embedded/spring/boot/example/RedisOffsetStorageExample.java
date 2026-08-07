package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.configurer.storage.OffsetStorageType;
import io.debezium.embedded.configurer.storage.RedisOffsetStorageConfigurer;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Redis Offset 存储示例
 * 
 * <p>该示例展示了如何使用 Redis 作为 Debezium 的 Offset 存储。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *   <li>Offset Store: 使用 Redis 存储连接器的偏移量信息</li>
 *   <li>支持 SSL/TLS 连接</li>
 *   <li>支持 Redis 6.0+ ACL 认证</li>
 *   <li>支持连接池和重试机制</li>
 * </ul>
 * 
 * <p>注意：Schema History 配置现在在 {@link io.debezium.embedded.configurer.history.RedisSchemaHistoryConfigurer} 中处理</p>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">Debezium Storage Configuration</a>
 */
@SpringBootApplication
public class RedisOffsetStorageExample {

    public static void main(String[] args) {
        SpringApplication.run(RedisOffsetStorageExample.class, args);
    }

    /**
     * 配置 Redis Offset 存储属性
     * 
     * <p>该配置严格按照 Debezium 官方文档中的 Redis 配置参数进行设置，包括：</p>
     * <ul>
     *   <li>Offset Store 配置（address, database, password 等）</li>
     *   <li>SSL/TLS 安全配置</li>
     *   <li>连接池和超时配置</li>
     *   <li>重试和等待配置</li>
     * </ul>
     */
    @Bean
    public DebeziumOffsetStorageProperties redisOffsetStorageProperties() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        
        // 设置存储类型为 Redis
        properties.setType(OffsetStorageType.REDIS);
        
        // 配置 Redis 连接参数
        DebeziumOffsetStorageProperties.Redis redis = properties.getRedis();
        
        // ==================== Offset Store 配置 ====================
        // 基本连接配置
        redis.setKey("metadata:debezium:offsets");  // Redis 键名，默认值
        redis.setAddress("localhost:6379");  // Redis 服务器地址
        redis.setUser("");  // Redis 用户名（可选）
        redis.setPassword("");  // Redis 密码（可选）
        redis.setDbIndex(0);  // Redis 数据库索引，默认值：0
        
        // SSL/TLS 配置（生产环境建议启用）
        redis.setSslEnabled(false);  // 是否启用 SSL/TLS
        redis.setSslHostnameVerificationEnabled(false);  // SSL 主机名验证是否启用
        redis.setSslTruststorePath("");  // SSL 信任库路径
        redis.setSslTruststorePassword("");  // SSL 信任库密码
        redis.setSslTruststoreType("JKS");  // SSL 信任库类型
        redis.setSslKeystorePath("");  // SSL 密钥库路径
        redis.setSslKeystorePassword("");  // SSL 密钥库密码
        redis.setSslKeystoreType("JKS");  // SSL 密钥库类型
        
        // 超时配置
        redis.setConnectionTimeoutMs(2000);  // 连接超时时间（毫秒）
        redis.setSocketTimeoutMs(2000);  // Socket 超时时间（毫秒）
        
        // 重试配置
        redis.setRetryInitialDelayMs(300);  // 重试初始延迟时间（毫秒）
        redis.setRetryMaxDelayMs(10000);  // 重试最大延迟时间（毫秒）
        redis.setRetryMaxAttempts(10);  // 重试最大尝试次数
        
        // 等待配置
        redis.setWaitEnabled(false);  // 等待启用
        redis.setWaitTimeoutMs(1000);  // 等待超时时间（毫秒）
        redis.setWaitRetryEnabled(false);  // 等待重试启用
        redis.setWaitRetryDelayMs(1000);  // 等待重试延迟时间（毫秒）
        
        return properties;
    }

    /**
     * 配置 Redis Offset 存储配置器
     * 
     * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium 配置，
     * 严格按照官方文档中的参数名称进行映射。</p>
     */
    @Bean
    public RedisOffsetStorageConfigurer redisOffsetStorageConfigurer() {
        return new RedisOffsetStorageConfigurer();
    }
}

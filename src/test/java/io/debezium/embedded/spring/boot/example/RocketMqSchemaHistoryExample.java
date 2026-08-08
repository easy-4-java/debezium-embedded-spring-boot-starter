package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.configurer.history.RocketMqSchemaHistoryConfigurer;
import io.debezium.embedded.configurer.history.SchemaHistoryType;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * RocketMQ Schema History 存储示例
 * 
 * <p>该示例展示了如何使用 Apache RocketMQ 作为 Debezium 的 Schema History 存储。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *   <li>Schema History: 使用 Apache RocketMQ 存储数据库模式变更历史</li>
 *   <li>支持 ACL 访问控制</li>
 *   <li>支持 NameServer 发现服务</li>
 *   <li>支持恢复和超时配置</li>
 * </ul>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">Debezium Storage Configuration</a>
 */
@SpringBootApplication
public class RocketMqSchemaHistoryExample {

    public static void main(String[] args) {
        SpringApplication.run(RocketMqSchemaHistoryExample.class, args);
    }

    /**
     * 配置 RocketMQ Schema History 存储属性
     * 
     * <p>该配置严格按照 Debezium 官方文档中的 RocketMQ Schema History 配置参数进行设置，包括：</p>
     * <ul>
     *   <li>Schema History 配置（topic, name.srv.addr 等）</li>
     *   <li>ACL 认证配置</li>
     *   <li>恢复和超时配置</li>
     * </ul>
     */
    @Bean
    public DebeziumSchemaHistoryProperties rocketMqSchemaHistoryProperties() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        
        // 设置历史记录类型为 RocketMQ
        properties.setType(SchemaHistoryType.ROCKETMQ);
        
        // 配置 RocketMQ 连接参数
        DebeziumSchemaHistoryProperties.RocketMq rocketMq = properties.getRocketMq();
        
        // ==================== Schema History 配置 ====================
        // 必需配置
        rocketMq.setTopic("debezium-schema-history");  // 必须显式配置，无默认值
        rocketMq.setNameSrvAddr("localhost:9876");     // 必须显式配置，无默认值
        
        // ACL 配置（可选）
        rocketMq.setAclEnabled(false);  // 是否启用 ACL
        rocketMq.setAccessKey("");      // RocketMQ 访问密钥
        rocketMq.setSecretKey("");      // RocketMQ 秘密密钥
        
        // 恢复和超时配置（可选）
        rocketMq.setRecoveryAttempts(100);           // 恢复尝试次数
        rocketMq.setRecoveryPollIntervalMs(1000);    // 恢复轮询间隔（毫秒）
        rocketMq.setStoreRecordTimeoutMs(5000);      // 存储记录超时时间（毫秒）
        
        return properties;
    }

    /**
     * 配置 RocketMQ Schema History 配置器
     * 
     * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium 配置，
     * 严格按照官方文档中的参数名称进行映射。</p>
     */
    @Bean
    public RocketMqSchemaHistoryConfigurer rocketMqSchemaHistoryConfigurer() {
        return new RocketMqSchemaHistoryConfigurer();
    }
}

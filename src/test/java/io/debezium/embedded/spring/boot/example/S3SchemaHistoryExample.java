package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.configurer.history.AmazonS3SchemaHistoryConfigurer;
import io.debezium.embedded.configurer.history.SchemaHistoryType;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * S3 Schema History 存储示例
 * 
 * <p>该示例展示了如何使用 Amazon S3 作为 Debezium 的 Schema History 存储。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *   <li>Schema History: 使用 Amazon S3 存储数据库模式变更历史</li>
 *   <li>支持 AWS 认证</li>
 *   <li>支持自定义端点</li>
 *   <li>支持区域配置</li>
 * </ul>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">Debezium Storage Configuration</a>
 */
@SpringBootApplication
public class S3SchemaHistoryExample {

    public static void main(String[] args) {
        SpringApplication.run(S3SchemaHistoryExample.class, args);
    }

    /**
     * 配置 S3 Schema History 存储属性
     * 
     * <p>该配置严格按照 Debezium 官方文档中的 S3 Schema History 配置参数进行设置，包括：</p>
     * <ul>
     *   <li>Schema History 配置（bucket.name, object.name 等）</li>
     *   <li>AWS 认证配置</li>
     *   <li>区域和端点配置</li>
     * </ul>
     */
    @Bean
    public DebeziumSchemaHistoryProperties s3SchemaHistoryProperties() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        
        // 设置历史记录类型为 S3
        properties.setType(SchemaHistoryType.S3);
        
        // 配置 S3 连接参数
        DebeziumSchemaHistoryProperties.S3 s3 = properties.getS3();
        
        // ==================== Schema History 配置 ====================
        // 必需配置
        s3.setBucketName("debezium-schema-history");  // 必须显式配置，无默认值
        s3.setObjectName("schema-history.json");      // 必须显式配置，无默认值
        
        // 认证配置（可选）
        s3.setAccessKeyId("");  // AWS 访问密钥 ID
        s3.setSecretAccessKey("");  // AWS 秘密访问密钥
        
        // 可选配置
        s3.setRegionName("us-east-1");  // AWS 区域
        s3.setEndpointUrl("");  // 自定义端点 URL
        
        return properties;
    }

    /**
     * 配置 S3 Schema History 配置器
     * 
     * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium 配置，
     * 严格按照官方文档中的参数名称进行映射。</p>
     */
    @Bean
    public AmazonS3SchemaHistoryConfigurer s3SchemaHistoryConfigurer() {
        return new AmazonS3SchemaHistoryConfigurer();
    }
}

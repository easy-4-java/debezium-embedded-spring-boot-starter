package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.configurer.history.AzureBlobSchemaHistoryConfigurer;
import io.debezium.embedded.configurer.history.SchemaHistoryType;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Azure Blob Storage Schema History 存储示例
 * 
 * <p>该示例展示了如何使用 Azure Blob Storage 作为 Debezium 的 Schema History 存储。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *   <li>Schema History: 使用 Azure Blob Storage 存储数据库模式变更历史</li>
 *   <li>支持 Azure 存储账户认证</li>
 *   <li>支持容器和 Blob 配置</li>
 *   <li>适用于 Azure HDInsight 服务</li>
 * </ul>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">Debezium Storage Configuration</a>
 */
@SpringBootApplication
public class AzureBlobSchemaHistoryExample {

    public static void main(String[] args) {
        SpringApplication.run(AzureBlobSchemaHistoryExample.class, args);
    }

    /**
     * 配置 Azure Blob Storage Schema History 存储属性
     * 
     * <p>该配置严格按照 Debezium 官方文档中的 Azure Blob Storage Schema History 配置参数进行设置，包括：</p>
     * <ul>
     *   <li>Schema History 配置（connection.string, account.name 等）</li>
     *   <li>Azure 存储账户配置</li>
     *   <li>容器和 Blob 配置</li>
     * </ul>
     */
    @Bean
    public DebeziumSchemaHistoryProperties azureBlobSchemaHistoryProperties() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        
        // 设置历史记录类型为 Azure Blob Storage
        properties.setType(SchemaHistoryType.AZURE_BLOB);
        
        // 配置 Azure Blob Storage 连接参数
        DebeziumSchemaHistoryProperties.AzureBlob azureBlob = properties.getAzureBlob();
        
        // ==================== Schema History 配置 ====================
        // 必需配置
        azureBlob.setConnectionString("DefaultEndpointsProtocol=https;AccountName=yourstorageaccount;AccountKey=yourstoragekey;EndpointSuffix=core.windows.net");  // 必须显式配置，无默认值
        azureBlob.setAccountName("yourstorageaccount");  // 必须显式配置，无默认值
        azureBlob.setContainerName("debezium-schema-history");  // 必须显式配置，无默认值
        azureBlob.setBlobName("schema-history.json");  // 必须显式配置，无默认值
        
        return properties;
    }

    /**
     * 配置 Azure Blob Storage Schema History 配置器
     * 
     * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium 配置，
     * 严格按照官方文档中的参数名称进行映射。</p>
     */
    @Bean
    public AzureBlobSchemaHistoryConfigurer azureBlobSchemaHistoryConfigurer() {
        return new AzureBlobSchemaHistoryConfigurer();
    }
}

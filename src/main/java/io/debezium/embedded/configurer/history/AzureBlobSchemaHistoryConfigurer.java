package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Azure Blob Storage 数据库历史记录配置器。
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class AzureBlobSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.AzureBlob azureBlob = properties.getAzureBlob();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        builder.with("schema.history.internal", "io.debezium.storage.azure.blob.history.AzureBlobSchemaHistory");
        
        // 严格按照官方文档配置参数
        map.from(azureBlob::getConnectionString).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.account.connectionstring", value));
        map.from(azureBlob::getAccountName).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.account.name", value));
        map.from(azureBlob::getContainerName).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.account.container.name", value));
        map.from(azureBlob::getBlobName).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.blob.name", value));
    }
}

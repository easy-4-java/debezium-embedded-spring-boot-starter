package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * {@link SchemaHistoryConfigurer} for Azure Blob Storage based schema history.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class AzureBlobSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * Applies the configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the configuration properties
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

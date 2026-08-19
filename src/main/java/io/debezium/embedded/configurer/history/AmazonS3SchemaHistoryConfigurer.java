package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import io.debezium.embedded.util.PropertyMappers;

/**
 * {@link SchemaHistoryConfigurer} for Amazon S3 based schema history.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class AmazonS3SchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * Applies the configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the configuration properties
     */
    @Override
    /**
     * <p>Apply.</p>
     * @param builder
     * @param properties
     */
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.S3 s3 = properties.getS3();
        
        builder.with("schema.history.internal", "io.debezium.storage.s3.history.S3SchemaHistory");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMappers.whenNonNull();
        
        // 严格按照官方文档配置参数
        // 必需的 S3 配置
        map.from(s3::getBucketName).whenHasText().to(value -> builder.with("schema.history.internal.s3.bucket.name", value));
        map.from(s3::getObjectName).whenHasText().to(value -> builder.with("schema.history.internal.s3.object.name", value));
        
        // 认证配置
        map.from(s3::getAccessKeyId).whenHasText().to(value -> builder.with("schema.history.internal.s3.access.key.id", value));
        map.from(s3::getSecretAccessKey).whenHasText().to(value -> builder.with("schema.history.internal.s3.secret.access.key", value));
        
        // 可选配置
        map.from(s3::getRegionName).whenHasText().to(value -> builder.with("schema.history.internal.s3.region.name", value));
        map.from(s3::getEndpointUrl).whenHasText().to(value -> builder.with("schema.history.internal.s3.endpoint", value));
    }
}

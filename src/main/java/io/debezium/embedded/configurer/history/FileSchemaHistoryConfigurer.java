package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import io.debezium.embedded.util.PropertyMappers;

/**
 * {@link SchemaHistoryConfigurer} for file based schema history.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class FileSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * Applies the configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the configuration properties
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {

        DebeziumSchemaHistoryProperties.File file = properties.getFile();

        // Internal schema history store
        builder.with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMappers.whenNonNull();
        map.from(file::getFilename).whenHasText().to(value -> builder.with("schema.history.internal.file", value));

    }
}

package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * {@link OffsetStorageConfigurer} for user-provided custom offset-storage backends.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CustomOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * Applies the storage configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the offset-storage configuration properties
     */
    @Override
    /**
     * <p>Apply.</p>
     * @param builder
     * @param properties
     */
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Custom custom = properties.getCustom();
        
        if (custom.getClassName() != null) {
            builder.with("offset.storage", custom.getClassName());
            
            // Forward custom raw properties
            if (custom.getProps() != null) {
                custom.getProps().forEach((key, value) -> {
                    if (key.startsWith("offset.storage.")) {
                        builder.with(key, value);
                    } else {
                        builder.with("offset.storage." + key, value);
                    }
                });
            }
        }
    }
}



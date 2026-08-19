package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import io.debezium.embedded.util.PropertyMappers;
import java.util.Objects;

/**
 * {@link ConnectorConfigurer} for user-provided custom connectors.
 * <p>Applies the connector class name declared on
 * {@link DebeziumConnectorProperties.Custom} and forwards any additional raw
 * properties to the builder.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CustomConnectorConfigurer implements ConnectorConfigurer {
    @Override
    /**
     * <p>Apply.</p>
     * @param builder
     * @param properties
     */
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMappers.whenNonNull();
        
        if (properties.getCustom() != null) {
            map.from(properties.getCustom()::getConnectorClass).whenHasText().to(value -> builder.with("connector.class", value));
            
            // Forward custom raw properties
            map.from(properties.getCustom()::getProps).when(Objects::nonNull).to(props -> {
                if (props != null) {
                    props.forEach(builder::with);
                }
            });
        }
    }
}

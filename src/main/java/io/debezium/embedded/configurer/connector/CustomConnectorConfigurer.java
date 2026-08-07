package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * 自定义连接器配置器。
 */
public class CustomConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        if (properties.getCustom() != null) {
            map.from(properties.getCustom()::getConnectorClass).whenHasText().to(value -> builder.with("connector.class", value));
            
            // 添加自定义配置属性
            map.from(properties.getCustom()::getProps).whenNonNull().to(props -> {
                if (props != null) {
                    props.forEach(builder::with);
                }
            });
        }
    }
}

package io.debezium.embedded.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Debezium 处理器注解，继承 @Component
 * 用于标注 Debezium 处理器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DebeziumEventHandler {

    /**
     * 继承 @Component 的 value 属性
     * @return String
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}

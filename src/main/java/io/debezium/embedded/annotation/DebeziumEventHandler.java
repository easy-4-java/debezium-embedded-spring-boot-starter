package io.debezium.embedded.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Marks a class as a Debezium event handler.
 * <p>Meta-annotated with {@link Component} so annotated beans are picked up
 * by component scanning and registered with the application context.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DebeziumEventHandler {

    /**
     * Alias for the {@link Component#value()} bean name.
     *
     * @return the bean name
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}

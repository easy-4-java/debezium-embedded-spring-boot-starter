package io.debezium.embedded.util;

import org.springframework.boot.context.properties.PropertyMapper;

import java.util.Objects;

/**
 * Convenience helpers around {@link PropertyMapper} that restore the
 * {@code alwaysApplyingWhenNonNull()} shortcut removed in Spring Boot 4.x.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public final class PropertyMappers {

    private PropertyMappers() {
    }

    /**
     * Returns a {@link PropertyMapper} that silently drops {@code null} source
     * values, equivalent to the former
     * {@code PropertyMapper.get().alwaysApplyingWhenNonNull()}.
     *
     * @return a property mapper that ignores {@code null} values
     */
    public static PropertyMapper whenNonNull() {
        return PropertyMapper.get().alwaysApplying(new PropertyMapper.SourceOperator() {
            @Override
            public <T> PropertyMapper.Source<T> apply(PropertyMapper.Source<T> source) {
                return source.when(Objects::nonNull);
            }
        });
    }
}

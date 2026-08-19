package io.debezium.embedded.annotation;


import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Holds a single {@link OnDebeziumEvent} annotated method along with its
 * target bean, used by the annotation dispatch path to invoke listeners.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Getter
/**
 * <p>Auto-configuration for DebeziumEventHolder.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumEventHolder {

    /** The target bean instance. */
    private final Object target;
    /** The annotated listener method. */
    private final Method method;
    /** The merged {@link OnDebeziumEvent} annotation. */
    private final OnDebeziumEvent event;

    /**
     * Creates a new holder binding a target, method and annotation.
     *
     * @param target the bean instance owning the method
     * @param method the listener method
     * @param event  the merged {@link OnDebeziumEvent} annotation
     */
    public DebeziumEventHolder(Object target, Method method, OnDebeziumEvent event) {
        this.target = target;
        this.method = method;
        this.event = event;
    }

    /**
     * @param eventType the event type to test
     * @return {@code true} when this holder matches the supplied event type
     *         (or the holder accepts any event type)
     */
    public boolean isMatch(DebeziumEntry.EventType eventType) {
        return this.getEvent().eventType().length == 0 || Arrays.stream(this.getEvent().eventType()).anyMatch(ev -> ev == eventType) || eventType == null;
    }

}

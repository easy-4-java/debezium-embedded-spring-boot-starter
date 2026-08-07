package io.debezium.embedded.annotation.event;

import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 刪除表操作监听器
 *
 * @author lujun
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(eventType = DebeziumEntry.EventType.TRUNCATE)
public @interface OnTruncateTableEvent {
    /**
     * debezium 指令
     * default for all
     *  @return debezium destination
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String destination() default "";

    /**
     * 数据库实例
     * @return 数据库实例
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String schema();
}

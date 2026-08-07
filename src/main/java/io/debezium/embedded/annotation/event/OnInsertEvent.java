package io.debezium.embedded.annotation.event;

import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 新增操作监听器 发生insert时 会触发
 *
 * @author lujun
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(eventType = DebeziumEntry.EventType.CREATE)
public @interface OnInsertEvent {

    /**
     * debezium 指令
     * default for all
     * @return debezium destination
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String destination() default "";

    /**
     * 数据库实例
     * @return 数据库实例
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String schema();

    /**
     * 监听的表
     * default for all
     * @return table name
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String table();

}

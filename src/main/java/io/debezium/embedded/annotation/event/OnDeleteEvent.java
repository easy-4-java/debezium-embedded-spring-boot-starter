package io.debezium.embedded.annotation.event;

import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 删除操作监听器 当删除数据库的记录时 添加该注解的方法会被调用
 *
 * @author lujun
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(eventType = DebeziumEntry.EventType.DELETE)
public @interface OnDeleteEvent {

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
     * @return 监听的表
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String table();

}

package io.debezium.embedded.annotation;


import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 监听 debezium 操作
 */
@Getter
public class DebeziumEventHolder {

    /**
     * 目标 bean
     */
    private final Object target;
    /**
     * 监听的方法
     */
    private final Method method;
    /**
     * 监听的事件
     */
    private final OnDebeziumEvent event;

    /**
     * 构造方法，设置目标，方法以及注解类型
     * @param target Object 目标
     * @param method Method 方法
     * @param event OnDebeziumEvent 注解
     */
    public DebeziumEventHolder(Object target, Method method, OnDebeziumEvent event) {
        this.target = target;
        this.method = method;
        this.event = event;
    }

    public boolean isMatch(DebeziumEntry.EventType eventType) {
        return this.getEvent().eventType().length == 0 || Arrays.stream(this.getEvent().eventType()).anyMatch(ev -> ev == eventType) || eventType == null;
    }

}

package io.debezium.embedded.handler;

import io.debezium.engine.ChangeEvent;

import java.util.Properties;

/**
 * ChangeEvent 处理器
 */
@FunctionalInterface
public interface ChangeEventHandler {

    /**
     * 处理消息
     * @param changeEvent 数据变动事件对象
     * @param props 配置
     */
    void handleEvent(ChangeEvent<String, String> changeEvent, Properties props);

}

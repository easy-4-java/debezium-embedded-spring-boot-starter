package io.debezium.embedded.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * Canal 线程未捕获异常处理器
 */
@Slf4j
public class DebeziumThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("thread "+ t.getName()+" have a exception",e);
    }

}

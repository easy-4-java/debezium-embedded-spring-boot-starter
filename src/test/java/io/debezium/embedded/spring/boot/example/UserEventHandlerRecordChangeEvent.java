package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.spring.boot.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户表变更事件处理器示例
 */
@Slf4j
@Component
public class UserEventHandlerRecordChangeEvent implements RecordChangeEventEntryHandler<User> {

    @Override
    public void insert(User user) {
        log.info("用户新增: {}", user);
        // 处理用户新增逻辑
        // 例如：发送欢迎邮件、更新缓存等
    }

    @Override
    public void update(User before, User after) {
        log.info("用户更新: before={}, after={}", before, after);
        // 处理用户更新逻辑
        // 例如：更新缓存、发送通知等
    }

    @Override
    public void delete(User user) {
        log.info("用户删除: {}", user);
        // 处理用户删除逻辑
        // 例如：清理缓存、记录审计日志等
    }
}

package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.spring.boot.example.entity.Order;
import io.debezium.embedded.spring.boot.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * Debezium Embedded 使用示例
 * 
 * 配置说明：
 * 1. 在 application.yml 中配置 debezium.embedded.enabled=true
 * 2. 配置数据库连接信息
 * 3. 选择 Offset 存储类型（FILE/KAFKA/JDBC/REDIS/S3/CUSTOM）
 * 4. 实现 EntryHandler 接口处理数据变更事件
 * 
 * 数据流：
 * DebeziumEngine -> RecordChangeEventHandler -> EntryHandler -> 业务逻辑
 */
@Slf4j
@SpringBootApplication
public class EmbeddedDebeziumExample {

    public static void main(String[] args) {
        SpringApplication.run(EmbeddedDebeziumExample.class, args);
    }

    /**
     * 用户表变更事件处理器
     * 实现 EntryHandler<User> 接口，处理用户表的增删改事件
     */
    @Component
    public static class UserEventHandlerRecordChangeEvent implements RecordChangeEventEntryHandler<User> {

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

    /**
     * 订单表变更事件处理器示例
     */
    @Component
    public static class OrderEventHandlerRecordChangeEvent implements RecordChangeEventEntryHandler<Order> {

        @Override
        public void insert(Order order) {
            log.info("订单新增: {}", order);
            // 处理订单新增逻辑
        }

        @Override
        public void update(Order before, Order after) {
            log.info("订单更新: before={}, after={}", before, after);
            // 处理订单更新逻辑
        }

        @Override
        public void delete(Order order) {
            log.info("订单删除: {}", order);
            // 处理订单删除逻辑
        }
    }
}

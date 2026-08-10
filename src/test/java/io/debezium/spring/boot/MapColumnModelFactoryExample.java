package io.debezium.spring.boot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.debezium.embedded.factory.MapColumnModelFactory;
import io.debezium.embedded.handler.RowEntryHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * MapColumnModelFactory使用示例
 * 
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @date 2025-01-27
 */
public class MapColumnModelFactoryExample {

    public static void main(String[] args) {
        System.out.println("=== MapColumnModelFactory 使用示例 ===");
        
        // 创建MapColumnModelFactory实例
        MapColumnModelFactory factory = new MapColumnModelFactory();
        
        // 准备测试数据
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("id", 1L);
        inputMap.put("name", "张三");
        inputMap.put("age", 25);
        inputMap.put("email", "zhangsan@example.com");
        
        try {
            // 1. 转换为自定义JavaBean对象（有MyBatis-Plus注解）
            UserWithAnnotation userWithAnnotation = factory.newInstance(inputMap, new UserHandlerWithAnnotation());
            System.out.println("转换为有注解的User对象: " + userWithAnnotation);
            
            // 2. 转换为自定义JavaBean对象（无MyBatis-Plus注解）
            UserWithoutAnnotation userWithoutAnnotation = factory.newInstance(inputMap, new UserHandlerWithoutAnnotation());
            System.out.println("转换为无注解的User对象: " + userWithoutAnnotation);
            
            // 3. 转换为Map对象
            Map<String, Object> resultMap = factory.newInstance(inputMap, new MapHandler());
            System.out.println("转换为Map对象: " + resultMap);
            
            System.out.println("\n=== 所有测试通过 ===");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 有MyBatis-Plus注解的User实体类
    @TableName("user")
    public static class UserWithAnnotation {
        private Long id;
        
        @TableField("name")
        private String name;
        
        @TableField("age")
        private Integer age;
        
        @TableField("email")
        private String email;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        @Override
        public String toString() {
            return "UserWithAnnotation{id=" + id + ", name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }
    
    // 无MyBatis-Plus注解的User实体类
    public static class UserWithoutAnnotation {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        @Override
        public String toString() {
            return "UserWithoutAnnotation{id=" + id + ", name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }
    
    // 有注解的User处理器
    public static class UserHandlerWithAnnotation implements RowEntryHandler<UserWithAnnotation> {
        @Override
        public void insert(UserWithAnnotation entity) {
            System.out.println("插入用户: " + entity);
        }
        
        @Override
        public void update(UserWithAnnotation before, UserWithAnnotation after) {
            System.out.println("更新用户: " + before + " -> " + after);
        }
        
        @Override
        public void delete(UserWithAnnotation entity) {
            System.out.println("删除用户: " + entity);
        }
    }
    
    // 无注解的User处理器
    public static class UserHandlerWithoutAnnotation implements RowEntryHandler<UserWithoutAnnotation> {
        @Override
        public void insert(UserWithoutAnnotation entity) {
            System.out.println("插入用户: " + entity);
        }
        
        @Override
        public void update(UserWithoutAnnotation before, UserWithoutAnnotation after) {
            System.out.println("更新用户: " + before + " -> " + after);
        }
        
        @Override
        public void delete(UserWithoutAnnotation entity) {
            System.out.println("删除用户: " + entity);
        }
    }
    
    // Map处理器
    public static class MapHandler implements RowEntryHandler<Map<String, Object>> {
        @Override
        public void insert(Map<String, Object> entity) {
            System.out.println("插入Map: " + entity);
        }
        
        @Override
        public void update(Map<String, Object> before, Map<String, Object> after) {
            System.out.println("更新Map: " + before + " -> " + after);
        }
        
        @Override
        public void delete(Map<String, Object> entity) {
            System.out.println("删除Map: " + entity);
        }
    }
}

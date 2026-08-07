package io.debezium.embedded.spring.boot.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类示例
 */
@Data
@TableName("users")
public class User {
    
    @TableId
    private Long id;
    
    private String name;
    
    private String email;
    
    private String phone;
    
    private Integer status;
    
    private String createTime;
    
    private String updateTime;
}

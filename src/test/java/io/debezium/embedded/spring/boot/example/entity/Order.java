package io.debezium.embedded.spring.boot.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 订单实体类示例
 */
@Data
@TableName("orders")
public class Order {
    
    @TableId
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private String status;
    
    private String createTime;
    
    private String updateTime;
}

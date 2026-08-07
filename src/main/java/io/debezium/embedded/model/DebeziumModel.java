package io.debezium.embedded.model;


import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * debezium 消息模型
 */
@Setter
@Getter
@Builder
public class DebeziumModel {

    public static class ChangeListenerModel {
        /**
         * 当前DB
         */
        private String db;
        /**
         * 当前表
         */
        private String table;
        /**
         * 操作类型 1 add 2 update 3 delete
         */
        private Integer eventType;
        /**
         * 操作时间
         */
        private Long changeTime;
    }

    /**
     * 消息id
     */
    private long id;

    /**
     * 库名
     */
    private String destination;
    /**
     * 库名
     */
    private String schema;
    /**
     * 表名
     */
    private String table;
    /**
     * 事件类型
     */
    private DebeziumEntry.EventType eventType;
    /**
     * 现数据
     */
    private String data;
    /**
     * 之前数据
     */
    private String beforeData;
    /**
     * binlog changeTime
     */
    private Long changeTime;
    /**
     * dml build timeStamp
     */
    private Long createTime;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DebeziumModel{");
        sb.append("id=").append(id);
        sb.append(", schema='").append(schema).append('\'');
        sb.append(", table='").append(table).append('\'');
        sb.append(", eventType='").append(eventType).append('\'');
        sb.append(", changeTime=").append(changeTime);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }

}

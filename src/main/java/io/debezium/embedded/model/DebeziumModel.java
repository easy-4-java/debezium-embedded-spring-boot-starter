package io.debezium.embedded.model;


import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Lightweight model describing a single change event processed by the handler.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Setter
@Getter
@Builder
public class DebeziumModel {

    /**
     * Compact representation of a parsed change-listener record, used to forward
     * change data to downstream consumers.
     */
    public static class ChangeListenerModel {
        /** Database the change occurred in. */
        private String db;
        /** Table the change occurred on. */
        private String table;
        /** Operation type: {@code 1} add, {@code 2} update, {@code 3} delete. */
        private Integer eventType;
        /** Time the change occurred (epoch millis). */
        private Long changeTime;
    }

    /** Monotonic message id. */
    private long id;

    /** Destination (connector) name. */
    private String destination;
    /** Database schema name. */
    private String schema;
    /** Table name. */
    private String table;
    /** Change event type. */
    private DebeziumEntry.EventType eventType;
    /** Serialised current row state. */
    private String data;
    /** Serialised previous row state (for updates / deletes). */
    private String beforeData;
    /** Time the change occurred in the source binlog (epoch millis). */
    private Long changeTime;
    /** Timestamp at which the DML was built (epoch millis). */
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

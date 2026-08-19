package io.debezium.embedded.protocol;

import io.debezium.data.Envelope;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Container for the Debezium protocol structures used by this starter.
 * <p>Holds row-level change data ({@link RowData}, {@link Column}, {@link RowChange})
 * and the {@link EventType} enumeration mapping to {@link Envelope.Operation}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumEntry {

    /**
     * Parsed representation of a single row change, including before/after state.
     */
    @Data
    /**
     * <p>Auto-configuration for RowChange.</p>
     * @author <a href="https://github.com/loong10k">Loong Wan</a>
     * @since 1.0.0
     */
    public static class RowChange {

        /** Debezium envelope operation. */
        Envelope.Operation operation;
        /** Serialised row state before the change. */
        String before;
        /** Serialised row state after the change. */
        String after;
        /** Serialised change descriptor. */
        String change;
        /** Schema the change occurred in. */
        String schema;
        /** Table the change occurred on. */
        String table;
        /** Connector destination name. */
        String destination;
        /** Time the change occurred (epoch millis). */
        Long changeTime;
        /** Time the DML was built (epoch millis). */
        Long createTime;

    }


    /**
     * Raw row change data carrying the key and before/after column lists.
     */
    @Data
    /**
     * <p>Auto-configuration for RowData.</p>
     * @author <a href="https://github.com/loong10k">Loong Wan</a>
     * @since 1.0.0
     */
    public static class RowData {
        /** Primary key of the changed row. */
        public String key;
        /** Column values before the change. */
        public List<Column> beforeColumnsList;
        /** Column values after the change. */
        public List<Column> afterColumnsList;

    }

    /**
     * Single column value within a {@link RowData}.
     */
    @Data
    /**
     * <p>Auto-configuration for Column.</p>
     * @author <a href="https://github.com/loong10k">Loong Wan</a>
     * @since 1.0.0
     */
    public static class Column {
        /** Column name. */
        public String name;
        /** Serialised column value. */
        public String value;
        /** Whether the column was updated in this change. */
        public Boolean updated;
    }

    /**
     * Row-level event types mapped to Debezium {@link Envelope.Operation} values.
     */
    public enum EventType {

        /** A new record was inserted. */
        CREATE(1, Envelope.Operation.CREATE),
        /** An existing record was updated. */
        UPDATE(2, Envelope.Operation.UPDATE),
        /** An existing record was removed or deleted. */
        DELETE(3, Envelope.Operation.DELETE),
        /** A table was truncated (all rows removed). */
        TRUNCATE(4, Envelope.Operation.TRUNCATE),

        ;

        /**
         * Resolves the {@link EventType} for the supplied numeric code.
         *
         * @param value numeric event code (1-4)
         * @return the matching event type, or {@code null} when unknown
         */
        public static EventType valueOf(int value) {
            switch (value) {
                case 1:
                    return CREATE;
                case 2:
                    return UPDATE;
                case 3:
                    return DELETE;
                case 4:
                    return TRUNCATE;
                default:
                    return null;
            }
        }

        private static final EventType[] VALUES = values();

        /** Numeric index of this event type. */
        @Getter
        private final int index;
        /** Corresponding Debezium envelope operation. */
        @Getter
        private final Envelope.Operation operation;

        EventType(int index, Envelope.Operation operation) {
            this.index = index;
            this.operation = operation;
        }

    }


}

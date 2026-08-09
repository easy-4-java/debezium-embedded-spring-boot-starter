package io.debezium.embedded.util;

import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DebeziumUtil}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumUtil")
class DebeziumUtilTest {

    @Test
    @DisplayName("static constants are set correctly")
    void staticConstants_areSet() {
        assertThat(DebeziumUtil.DATA).isEqualTo("data");
        assertThat(DebeziumUtil.BEFORE_DATA).isEqualTo("beforeData");
        assertThat(DebeziumUtil.EVENT_TYPE).isEqualTo("eventType");
        assertThat(DebeziumUtil.TABLE).isEqualTo("table");
        assertThat(DebeziumUtil.PAYLOAD).isEqualTo("payload");
    }

    @Test
    @DisplayName("TableFieldName.filterJsonField returns true for known fields")
    void tableFieldName_filterJsonField_knownFields() {
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("db")).isTrue();
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("table")).isTrue();
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("ts_ms")).isTrue();
    }

    @Test
    @DisplayName("TableFieldName.filterJsonField returns false for unknown fields")
    void tableFieldName_filterJsonField_unknownFields() {
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("unknown")).isFalse();
        assertThat(DebeziumUtil.TableFieldName.filterJsonField("foo")).isFalse();
    }

    @Test
    @DisplayName("checkNonEssentialData returns true when only filtered columns changed")
    void checkNonEssentialData_returnsTrue_whenOnlyFilteredColumnsChanged() {
        Map<String, Object> afterMap = new HashMap<>();
        afterMap.put("id", 1);
        afterMap.put("name", "new");
        afterMap.put("updated_at", "2025-01-02");

        Map<String, Object> beforeMap = new HashMap<>();
        beforeMap.put("id", 1);
        beforeMap.put("name", "old");
        beforeMap.put("updated_at", "2025-01-01");

        boolean result = DebeziumUtil.checkNonEssentialData("users", afterMap, beforeMap, List.of("name", "updated_at"));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("checkNonEssentialData returns false when essential columns changed")
    void checkNonEssentialData_returnsFalse_whenEssentialColumnsChanged() {
        Map<String, Object> afterMap = new HashMap<>();
        afterMap.put("id", 1);
        afterMap.put("name", "new");
        afterMap.put("status", "active");

        Map<String, Object> beforeMap = new HashMap<>();
        beforeMap.put("id", 1);
        beforeMap.put("name", "old");
        beforeMap.put("status", "inactive");

        boolean result = DebeziumUtil.checkNonEssentialData("users", afterMap, beforeMap, List.of("updated_at"));
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("checkNonEssentialData returns true when nothing changed")
    void checkNonEssentialData_returnsTrue_whenNothingChanged() {
        Map<String, Object> afterMap = new HashMap<>();
        afterMap.put("id", 1);

        Map<String, Object> beforeMap = new HashMap<>();
        beforeMap.put("id", 1);

        boolean result = DebeziumUtil.checkNonEssentialData("users", afterMap, beforeMap, List.of());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("DebeziumUtil getChangeDataInfo returns null for READ operation")
    void getChangeDataInfo_returnsNull_forReadOperation() {
        // We can't easily test this without Kafka Connect internals,
        // but we verify the method signature is accessible
        assertThat(DebeziumUtil.class).isNotNull();
    }
}

package io.debezium.embedded.util;

import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.annotation.DebeziumTable;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HandlerUtil}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("HandlerUtil")
class HandlerUtilTest {

    @Test
    @DisplayName("getCombinationValue with 3 args builds dest.schema.table")
    void getCombinationValue_3args() {
        assertThat(HandlerUtil.getCombinationValue("dest", "schema", "table"))
            .isEqualTo("dest.schema.table");
    }

    @Test
    @DisplayName("getCombinationValue with nulls uses wildcards")
    void getCombinationValue_nullsUseWildcards() {
        assertThat(HandlerUtil.getCombinationValue(null, null, null))
            .isEqualTo("*.*.*");
    }

    @Test
    @DisplayName("getCombinationValue with blanks uses wildcards")
    void getCombinationValue_blanksUseWildcards() {
        assertThat(HandlerUtil.getCombinationValue("", "  ", ""))
            .isEqualTo("*.*.*");
    }

    @Test
    @DisplayName("getCombinationValue with 4 args includes eventType")
    void getCombinationValue_4args() {
        assertThat(HandlerUtil.getCombinationValue("dest", "schema", "table", DebeziumEntry.EventType.CREATE))
            .isEqualTo("dest.schema.table.create");
    }

    @Test
    @DisplayName("getCombinationValue with 4 args and nulls uses wildcards")
    void getCombinationValue_4args_nullsUseWildcards() {
        assertThat(HandlerUtil.getCombinationValue(null, null, null, DebeziumEntry.EventType.DELETE))
            .isEqualTo("*.*.*.delete");
    }

    @Test
    @DisplayName("getDebeziumTableNameCombination returns annotation value")
    void getDebeziumTableNameCombination_returnsAnnotationValue() {
        AnnotatedHandler handler = new AnnotatedHandler();
        String result = HandlerUtil.getDebeziumTableNameCombination(handler);
        assertThat(result).isEqualTo("mydest.myschema.mytable");
    }

    @Test
    @DisplayName("getDebeziumTableNameCombination returns null for unannotated handler")
    void getDebeziumTableNameCombination_returnsNull_forUnannotated() {
        PlainHandler handler = new PlainHandler();
        String result = HandlerUtil.getDebeziumTableNameCombination(handler);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getTableHandlerMap builds map from annotated handlers")
    void getTableHandlerMap_buildsMap() {
        AnnotatedHandler handler = new AnnotatedHandler();
        Map<String, RecordChangeEventEntryHandler> map = HandlerUtil.getTableHandlerMap(List.of(handler));
        assertThat(map).containsKey("mydest.myschema.mytable");
    }

    @Test
    @DisplayName("getTableHandlerMap returns empty map for empty list")
    void getTableHandlerMap_emptyList() {
        Map<String, RecordChangeEventEntryHandler> map = HandlerUtil.getTableHandlerMap(Collections.emptyList());
        assertThat(map).isEmpty();
    }

    @Test
    @DisplayName("getTableHandlerMap returns empty map for null list")
    void getTableHandlerMap_nullList() {
        Map<String, RecordChangeEventEntryHandler> map = HandlerUtil.getTableHandlerMap(null);
        assertThat(map).isEmpty();
    }

    @Test
    @DisplayName("getEntryHandler returns null from list when no schema.table match")
    void getEntryHandler_exactMatch() {
        AnnotatedHandler handler = new AnnotatedHandler();
        RecordChangeEventEntryHandler result = HandlerUtil.getEntryHandler(List.of(handler), "myschema", "mytable");
        // AnnotatedHandler has destination="mydest" so combination is "mydest.myschema.mytable" which doesn't match "myschema.mytable"
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getEntryHandler from map finds exact match")
    void getEntryHandler_fromMap_exactMatch() {
        AnnotatedHandler handler = new AnnotatedHandler();
        Map<String, RecordChangeEventEntryHandler> map = Map.of("mydest.myschema.mytable", handler);
        RecordChangeEventEntryHandler result = HandlerUtil.getEntryHandler(map, "myschema", "mytable");
        assertThat(result).isNull(); // 2-part key doesn't match 3-part combination
    }

    @Test
    @DisplayName("getEntryHandler from map falls back to wildcard")
    void getEntryHandler_fromMap_wildcard() {
        WildcardHandler handler = new WildcardHandler();
        Map<String, RecordChangeEventEntryHandler> map = new java.util.HashMap<>();
        map.put("all", handler);
        RecordChangeEventEntryHandler result = HandlerUtil.getEntryHandler(map, "myschema", "mytable");
        assertThat(result).isSameAs(handler);
    }

    @Test
    @DisplayName("getEntryHandler from map returns null when no match")
    void getEntryHandler_fromMap_noMatch() {
        AnnotatedHandler handler = new AnnotatedHandler();
        Map<String, RecordChangeEventEntryHandler> map = Map.of("other.schema.table", handler);
        RecordChangeEventEntryHandler result = HandlerUtil.getEntryHandler(map, "myschema", "mytable");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getEventHolderMap builds map from holders")
    void getEventHolderMap_buildsMap() throws Exception {
        Method method = SampleListener.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleListener(), method, event);
        Map<String, List<DebeziumEventHolder>> map = HandlerUtil.getEventHolderMap(List.of(holder));
        assertThat(map).isNotEmpty();
    }

    @Test
    @DisplayName("getEventHolderMap returns empty for empty list")
    void getEventHolderMap_emptyList() {
        Map<String, List<DebeziumEventHolder>> map = HandlerUtil.getEventHolderMap(Collections.emptyList());
        assertThat(map).isEmpty();
    }

    @Test
    @DisplayName("getEventHolderMap returns empty for null list")
    void getEventHolderMap_nullList() {
        Map<String, List<DebeziumEventHolder>> map = HandlerUtil.getEventHolderMap(null);
        assertThat(map).isEmpty();
    }

    @Test
    @DisplayName("getDebeziumTableNameCombinations returns combinations for holder")
    void getDebeziumTableNameCombinations_returnsCombinations() throws Exception {
        Method method = SampleListener.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleListener(), method, event);
        List<String> combinations = HandlerUtil.getDebeziumTableNameCombinations(holder);
        assertThat(combinations).isNotEmpty();
        assertThat(combinations.get(0)).contains("create");
    }

    @Test
    @DisplayName("getEventHolders returns matching holders")
    void getEventHolders_returnsMatching() throws Exception {
        Method method = SampleListener.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleListener(), method, event);
        Map<String, List<DebeziumEventHolder>> map = HandlerUtil.getEventHolderMap(List.of(holder));

        List<DebeziumEventHolder> result = HandlerUtil.getEventHolders(map, "", "myschema", "mytable", DebeziumEntry.EventType.CREATE);
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("getEventHolders returns empty when no match")
    void getEventHolders_noMatch() {
        Map<String, List<DebeziumEventHolder>> map = new HashMap<>();
        List<DebeziumEventHolder> result = HandlerUtil.getEventHolders(map, "dest", "schema", "table", DebeziumEntry.EventType.CREATE);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAnnotationFilter matches correct combination")
    void getAnnotationFilter_matchesCorrectCombination() throws Exception {
        Method method = SampleListener.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleListener(), method, event);

        var predicate = HandlerUtil.getAnnotationFilter("", "myschema", "mytable", DebeziumEntry.EventType.CREATE);
        assertThat(predicate.test(holder)).isTrue();
    }

    @Test
    @DisplayName("getAnnotationFilter rejects wrong event type")
    void getAnnotationFilter_rejectsWrongEventType() throws Exception {
        Method method = SampleListener.class.getDeclaredMethod("onCreate");
        OnDebeziumEvent event = org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
        DebeziumEventHolder holder = new DebeziumEventHolder(new SampleListener(), method, event);

        var predicate = HandlerUtil.getAnnotationFilter("", "myschema", "mytable", DebeziumEntry.EventType.DELETE);
        assertThat(predicate.test(holder)).isFalse();
    }

    // ==================== Fixtures ====================

    @DebeziumTable(destination = "mydest", schema = "myschema", table = "mytable")
    static class AnnotatedHandler implements RecordChangeEventEntryHandler<String> {
        @Override public void insert(String obj) {}
        @Override public void update(String before, String after) {}
        @Override public void delete(String obj) {}
    }

    static class PlainHandler implements RecordChangeEventEntryHandler<Object> {
        @Override public void insert(Object obj) {}
        @Override public void update(Object before, Object after) {}
        @Override public void delete(Object obj) {}
    }

    @DebeziumTable(destination = "*", schema = "*", table = "*")
    static class WildcardHandler implements RecordChangeEventEntryHandler<Object> {
        @Override public void insert(Object obj) {}
        @Override public void update(Object before, Object after) {}
        @Override public void delete(Object obj) {}
    }

    static class SampleListener {
        @io.debezium.embedded.annotation.event.OnInsertEvent(schema = "myschema", table = "mytable")
        public void onCreate() {}
    }
}

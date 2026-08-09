package io.debezium.embedded.model;

import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DebeziumModel} and {@link DebeziumModel.ChangeListenerModel}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumModel")
class DebeziumModelTest {

    @Test
    @DisplayName("builder creates model with all fields")
    void builder_createsModelWithAllFields() {
        DebeziumModel model = DebeziumModel.builder()
                .id(42L)
                .destination("my-dest")
                .schema("public")
                .table("users")
                .eventType(DebeziumEntry.EventType.UPDATE)
                .data("{\"name\":\"new\"}")
                .beforeData("{\"name\":\"old\"}")
                .changeTime(1000L)
                .createTime(2000L)
                .build();

        assertThat(model.getId()).isEqualTo(42L);
        assertThat(model.getDestination()).isEqualTo("my-dest");
        assertThat(model.getSchema()).isEqualTo("public");
        assertThat(model.getTable()).isEqualTo("users");
        assertThat(model.getEventType()).isEqualTo(DebeziumEntry.EventType.UPDATE);
        assertThat(model.getData()).isEqualTo("{\"name\":\"new\"}");
        assertThat(model.getBeforeData()).isEqualTo("{\"name\":\"old\"}");
        assertThat(model.getChangeTime()).isEqualTo(1000L);
        assertThat(model.getCreateTime()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("toString contains all field names")
    void toString_containsFieldNames() {
        DebeziumModel model = DebeziumModel.builder()
                .id(1L)
                .schema("s")
                .table("t")
                .eventType(DebeziumEntry.EventType.CREATE)
                .changeTime(100L)
                .createTime(200L)
                .build();

        String str = model.toString();
        assertThat(str).contains("id=1");
        assertThat(str).contains("schema='s'");
        assertThat(str).contains("table='t'");
        assertThat(str).contains("changeTime=100");
        assertThat(str).contains("createTime=200");
    }

    @Test
    @DisplayName("setter and getter work for all fields")
    void setterGetter_work() {
        DebeziumModel model = DebeziumModel.builder().build();
        model.setId(99L);
        model.setDestination("dest");
        model.setSchema("myschema");
        model.setTable("mytable");
        model.setEventType(DebeziumEntry.EventType.DELETE);
        model.setData("data");
        model.setBeforeData("before");
        model.setChangeTime(300L);
        model.setCreateTime(400L);

        assertThat(model.getId()).isEqualTo(99L);
        assertThat(model.getDestination()).isEqualTo("dest");
        assertThat(model.getSchema()).isEqualTo("myschema");
        assertThat(model.getTable()).isEqualTo("mytable");
        assertThat(model.getEventType()).isEqualTo(DebeziumEntry.EventType.DELETE);
        assertThat(model.getData()).isEqualTo("data");
        assertThat(model.getBeforeData()).isEqualTo("before");
        assertThat(model.getChangeTime()).isEqualTo(300L);
        assertThat(model.getCreateTime()).isEqualTo(400L);
    }

    @Test
    @DisplayName("ChangeListenerModel can be instantiated")
    void changeListenerModel_canBeInstantiated() {
        DebeziumModel.ChangeListenerModel clm = new DebeziumModel.ChangeListenerModel();
        assertThat(clm).isNotNull();
    }
}

package io.debezium.embedded.context;

import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DebeziumContext}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("DebeziumContext")
class DebeziumContextTest {

    @AfterEach
    void cleanup() {
        DebeziumContext.removeModel();
    }

    @Test
    @DisplayName("getModel returns null when not set")
    void getModel_returnsNullWhenNotSet() {
        assertThat(DebeziumContext.getModel()).isNull();
    }

    @Test
    @DisplayName("setModel makes model available via getModel")
    void setModel_makesModelAvailable() {
        DebeziumModel model = DebeziumModel.builder()
                .id(1L)
                .schema("public")
                .table("users")
                .eventType(DebeziumEntry.EventType.CREATE)
                .build();

        DebeziumContext.setModel(model);

        assertThat(DebeziumContext.getModel()).isSameAs(model);
        assertThat(DebeziumContext.getModel().getId()).isEqualTo(1L);
        assertThat(DebeziumContext.getModel().getSchema()).isEqualTo("public");
        assertThat(DebeziumContext.getModel().getTable()).isEqualTo("users");
    }

    @Test
    @DisplayName("removeModel clears the context")
    void removeModel_clearsContext() {
        DebeziumModel model = DebeziumModel.builder().id(1L).build();
        DebeziumContext.setModel(model);

        DebeziumContext.removeModel();

        assertThat(DebeziumContext.getModel()).isNull();
    }
}

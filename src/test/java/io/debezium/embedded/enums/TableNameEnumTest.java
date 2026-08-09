package io.debezium.embedded.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TableNameEnum}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("TableNameEnum")
class TableNameEnumTest {

    @Test
    @DisplayName("ALL has wildcard components")
    void all_hasWildcardComponents() {
        assertThat(TableNameEnum.ALL.getDestination()).isEqualTo("*");
        assertThat(TableNameEnum.ALL.getSchema()).isEqualTo("*");
        assertThat(TableNameEnum.ALL.getTable()).isEqualTo("*");
    }

    @Test
    @DisplayName("DELIMITER is dot")
    void delimiter_isDot() {
        assertThat(TableNameEnum.DELIMITER.toString()).isEqualTo(".");
    }

    @Test
    @DisplayName("toString returns schema.table")
    void toString_returnsSchemaTable() {
        assertThat(TableNameEnum.ALL.toString()).isEqualTo("*.*");
    }
}

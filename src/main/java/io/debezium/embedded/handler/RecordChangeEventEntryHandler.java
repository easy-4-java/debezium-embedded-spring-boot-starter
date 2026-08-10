package io.debezium.embedded.handler;

/**
 * Callback interface invoked for each row-level change event.
 * <p>Implementations are registered per table and dispatched by the event
 * handlers when a matching {@code INSERT}, {@code UPDATE} or {@code DELETE}
 * is observed.</p>
 *
 * @param <R> the row model type
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface RecordChangeEventEntryHandler<R> {


    /** Default no-op callback for an inserted row. */
    default void insert(R t) {

    }


    /** Default no-op callback for an updated row, providing both old and new state. */
    default void update(R before, R after) {

    }


    /** Default no-op callback for a deleted row. */
    default void delete(R t) {

    }
}

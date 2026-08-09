package io.debezium.spring.boot;

import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.event.OnDeleteEvent;
import io.debezium.embedded.annotation.event.OnInsertEvent;
import io.debezium.embedded.annotation.event.OnUpdateEvent;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DebeziumEventHandler
public class DebeziumMessageEventHandler {

    private static final Logger log = LoggerFactory.getLogger(DebeziumMessageEventHandler.class);

    @OnInsertEvent(schema = "my_auth", table = "user_info")
    public void onTruncateTableEvent(DebeziumModel model, DebeziumEntry.RowChange rowChange) {
        log.info("onTruncateTableEvent");
    }

    @OnInsertEvent(schema = "my_auth", table = "user_info")
    public void onEventInsertData(DebeziumModel model, DebeziumEntry.RowChange rowChange) {
    }

    @OnUpdateEvent(schema = "my_auth", table = "user_info")
    public void onEventUpdateData(DebeziumModel model, DebeziumEntry.RowChange rowChange) {
        log.info("onEventUpdateData");
    }

    @OnDeleteEvent(schema = "my_auth", table = "user_info")
    public void onEventDeleteData(DebeziumEntry.RowChange rowChange, DebeziumModel model) {
        log.info("onEventDeleteData");
    }

}

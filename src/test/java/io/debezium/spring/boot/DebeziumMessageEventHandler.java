package io.debezium.spring.boot;

import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.event.OnDeleteEvent;
import io.debezium.embedded.annotation.event.OnInsertEvent;
import io.debezium.embedded.annotation.event.OnUpdateEvent;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.extern.slf4j.Slf4j;

@DebeziumEventHandler
@Slf4j
public class DebeziumMessageEventHandler {

    @OnInsertEvent(schema = "my_auth", table = "user_info")
    public void onTruncateTableEvent(DebeziumModel model, DebeziumEntry.RowChange rowChange) {
        log.info("onTruncateTableEvent");
    }

    @OnInsertEvent(schema = "my_auth", table = "user_info")
    public void onEventInsertData(DebeziumModel model, DebeziumEntry.RowChange rowChange) {

       /* // 1，获取当前事件的操作类型
        DebeziumEntry.EventType eventType = rowChange.getEventType();
        // 2,获取数据集
        List<DebeziumEntry.RowData> rowDatasList = rowChange.getRowDatasList();
        // 3,遍历RowDataList，并打印数据集
        for (DebeziumEntry.RowData rowData : rowDatasList) {
            JSONObject beforeData = new JSONObject();
            List<DebeziumEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (DebeziumEntry.Column column : beforeColumnsList) {
                beforeData.put(column.getName(), column.getValue());
            }
            JSONObject affterData = new JSONObject();
            List<DebeziumEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (DebeziumEntry.Column column : afterColumnsList) {
                affterData.put(column.getName(), column.getValue());
            }

            System.out.println("Table:" + model.getTable() +
                    ",EventType:" + eventType +
                    ",Before:" + beforeData +
                    ",After:" + affterData);
        }*/

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

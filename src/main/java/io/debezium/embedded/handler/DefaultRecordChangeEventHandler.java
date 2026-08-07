package io.debezium.embedded.handler;

import com.alibaba.fastjson2.JSON;
import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.context.DebeziumContext;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.embedded.util.DebeziumUtil;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class DefaultRecordChangeEventHandler implements RecordChangeEventHandler, ApplicationContextAware {

    /**
     * 通过注解方式的表数据变更处理器
     */
    private Map<String, List<DebeziumEventHolder>> tableEventHolderMap;
    /**
     * 表处理器
     */
    private Map<String, RecordChangeEventEntryHandler> tableHandlerMap;
    /**
     * 行数据处理器
     */
    private RowDataHandler<List<Map<String, String>>> rowDataHandler;

    public DefaultRecordChangeEventHandler(List<? extends RecordChangeEventEntryHandler> entryHandlers,
                                           RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleEvent(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                            DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter,
                            Properties props) {
        if(CollectionUtils.isEmpty(recordChangeEvents)){
            log.warn("Not found record change events, skip");
            return;
        }
        log.info("Received {} record change events", recordChangeEvents.size());
        for (RecordChangeEvent<SourceRecord> event: recordChangeEvents) {
            SourceRecord sourceRecord = event.record();
            Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
            if (Objects.isNull(sourceRecordChangeValue)) {
                log.warn("Source record value is null, skip");
                continue;
            }
            // 获取变更表数据
            Map<String, Object> changeMap = DebeziumUtil.getChangeTableInfo(sourceRecordChangeValue);
            if (CollectionUtils.isEmpty(changeMap)) {
                log.warn("Change map is empty, skip");
                continue;
            }
            DebeziumModel.ChangeListenerModel changeListenerModel = DebeziumUtil.getChangeDataInfo(sourceRecordChangeValue, changeMap);
            if (changeListenerModel == null) {
                log.warn("Change listener model is null, skip");
                continue;
            }
            String jsonString = JSON.toJSONString(changeListenerModel);
            log.info("Send change data: {}", jsonString);
            /*try {
                // 获取表对应的注解处理器
                List<DebeziumEventHolder> eventHolders = HandlerUtil.getEventHolders(tableEventHolderMap, destination, schemaName, tableName, eventType);
                if(!CollectionUtils.isEmpty(eventHolders)){
                    DebeziumModel model = DebeziumModel.builder()
                            .id(flatMessage.getId())
                            .schema(schemaName)
                            .table(tableName)
                            .eventType(eventType)
                            .executeTime(flatMessage.getEs())
                            .createTime(flatMessage.getTs()).build();
                    for (DebeziumEventHolder eventHolder : eventHolders) {
                        this.handlerRowData(model, maps, eventHolder, eventType);
                    }
                    continue;
                }
                // 获取表对应的处理器
                RecordChangeEventEntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, schemaName, tableName);
                // 判断是否有对应的处理器
                if(Objects.nonNull(entryHandler)){
                    DebeziumModel model = DebeziumModel.builder()
                            .id(flatMessage.getId())
                            .schema(schemaName)
                            .table(tableName)
                            .eventType(eventType)
                            .executeTime(flatMessage.getEs())
                            .createTime(flatMessage.getTs()).build();
                    this.handlerRowData(model, maps, entryHandler, eventType);
                }
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + maps.toString(), e);
            }*/
        }
        try {
            recordCommitter.markBatchFinished();
        } catch (Exception e) {
            log.error("Commit batch has an error", e);
        }
    }


    public void handlerRowData(DebeziumModel model, List<Map<String, String>> rowData, DebeziumEventHolder eventHolder, DebeziumEntry.EventType eventType) throws Exception {
        Method method = eventHolder.getMethod();
        try {
            DebeziumContext.setModel(model);
            ReflectionUtils.makeAccessible(method);
            Object[] args = GenericUtil.getInvokeArgs(method, model, rowData, eventType);
            method.invoke(eventHolder.getTarget(), args);
        } finally {
            // 移除上下文
            DebeziumContext.removeModel();
        }
    }

    public void handlerRowData(DebeziumModel model, List<Map<String, String>> rowData, RecordChangeEventEntryHandler entryHandler, DebeziumEntry.EventType eventType) throws Exception {
        try {
            // 设置上下文
            DebeziumContext.setModel(model);
            // 逐行调用Handler处理
            rowDataHandler.handlerRowData(rowData, entryHandler, eventType);
        } finally {
            // 移除上下文
            DebeziumContext.removeModel();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("{}: annotation event handler is initializing....", Thread.currentThread().getName());
        // 获取所有的处理器
        Map<String, Object> eventHandlerMap = applicationContext.getBeansWithAnnotation(DebeziumEventHandler.class);
        if(CollectionUtils.isEmpty(eventHandlerMap)){
            log.info("{}: not found annotation event handler.", Thread.currentThread().getName());
            return;
        }
        // 注解处理器对象
        List<DebeziumEventHolder> eventHolders = new ArrayList<>();
        for (Object target : eventHandlerMap.values()) {
            // 获取对象声明的方法
            Method[] methods = ReflectionUtils.getDeclaredMethods(target.getClass());
            for (Method method : methods) {
                OnDebeziumEvent debeziumEvent = AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
                if (Objects.nonNull(debeziumEvent)) {
                    eventHolders.add(new DebeziumEventHolder(target, method, debeziumEvent));
                }
            }
        }
        this.tableEventHolderMap = HandlerUtil.getEventHolderMap(eventHolders);
        log.info("{}: annotation event handler initialized finish.", Thread.currentThread().getName());
    }

}

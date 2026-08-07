package io.debezium.embedded.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.util.DebeziumUtil;
import io.debezium.embedded.util.HandlerUtil;
import io.debezium.engine.ChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;


@Slf4j
public class DefaultChangeEventHandler implements ChangeEventHandler, ApplicationContextAware {

    /**
     * 指定订阅的事件类型，主要用于标识事务的开始，变更数据，结束
    private List<DebeziumEntry.EntryType> subscribeTypes = Arrays.asList(DebeziumEntry.EntryType.ROWDATA);
     */
    /**
     * 通过注解方式的表数据变更处理器
     */
    private Map<String, List<DebeziumEventHolder>> tableEventHolderMap;
    /**
     * 表数据变更处理器
     */
    private Map<String, RecordChangeEventEntryHandler> tableHandlerMap;
    /**
     * 行数据处理器
     */
    /**
     * 行数据处理器
     */
    private RowDataHandler<List<Map<String, String>>> rowDataHandler;

    public DefaultChangeEventHandler(List<? extends RecordChangeEventEntryHandler> entryHandlers,
                                           RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }


    @Override
    public void handleEvent(ChangeEvent<String, String> event, Properties props) {
        if (Objects.nonNull(event.value())) {
            try {
                log.info("解析变更事件, event:{}}", event);
                // 解析JSON字符串
                JSONObject jsonValue = JSON.parseObject(event.value());
                JSONObject payload = jsonValue.getJSONObject(DebeziumUtil.PAYLOAD);
                if (payload != null) {
                    // 设置操作类型
                    String handleType = JSON.parseObject(JSON.toJSONString(payload.get("op")), String.class);

                   /* // 判断当前entryType类型是否订阅
                    if (this.isSubscribed(entryType)) {


                    }
                    message.setDataType(handleType);
                    // 设置变更前后的数据
                    JSONObject beforeData = payload.getJSONObject("before");
                    if (beforeData != null) {
                        message.setBeforeData(beforeData.toJSONString());
                    }
                    JSONObject afterData = payload.getJSONObject("after");
                    if (afterData != null) {
                        message.setAfterData(afterData.toJSONString());
                    }
                    // 设置数据库名称和表名称
                    JSONObject source = payload.getJSONObject("source");
                    // 获取数据库实例
                    String schemaName = source.getString("table");

                    if (source != null) {
                        message.setDatabaseName(source.getString("db"));
                        message.setTableName(source.getString("table"));
                        // 设置数据库类型为MySQL
                        message.setDbType(props.getProperty("database.dbType"));
                        // 设置偏移量
                        Long offset = source.getLong("pos");
                        if (offset != null) {
                            message.setOffset(offset);
                        }
                    }
                    // 这里可以添加对message的后续处理，例如发送到消息队列等
                    log.info("解析变更事件成功: {}", message);
                    SyncDataStrategy strategy = syncDataStrategyRouter.switchStrategy(message.getTableName());
                    if(Objects.isNull(strategy)){
                        log.error("未找到当前数据表的处理器");
                    }else{
                        strategy.syncTableData(message);
                    }*/
                }
            } catch (Exception e) {
                log.error("解析变更事件失败: {}", e.getMessage());
            }
        }

/*
        // 遍历 entryes，单条解析
        for (DebeziumEntry.Entry entry : message.getEntries()) {
            // 获取类型
            DebeziumEntry.EntryType entryType = entry.getEntryType();
            // 判断当前entryType类型是否订阅
            if (this.isSubscribed(entryType)) {
                // 获取数据库实例
                String schemaName = entry.getHeader().getSchemaName();
                // 获取表名
                String tableName = entry.getHeader().getTableName();
                try {
                    // 获取序列化后的数据
                    DebeziumEntry.RowChange rowChange = DebeziumEntry.RowChange.parseFrom(entry.getStoreValue());
                    // 获取当前事件的操作类型
                    DebeziumEntry.EventType eventType = rowChange.getEventType();
                    // 获取表对应的注解处理器
                    List<DebeziumEventHolder> eventHolders = HandlerUtil.getEventHolders(tableEventHolderMap, destination, schemaName, tableName, eventType);
                    if(!CollectionUtils.isEmpty(eventHolders)){
                        DebeziumModel model = DebeziumModel.builder()
                                .id(message.getId())
                                .schema(schemaName)
                                .table(tableName)
                                .eventType(eventType)
                                .executeTime(entry.getHeader().getExecuteTime())
                                .build();
                        for (DebeziumEventHolder eventHolder : eventHolders) {
                            this.handlerRowData(model, rowChange, eventHolder, eventType);
                        }
                        continue;
                    }
                    // 获取表对应的处理器
                    RecordChangeEventEntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, schemaName, tableName);
                    // 判断是否有对应的处理器
                    if(Objects.nonNull(entryHandler)){
                        DebeziumModel model = DebeziumModel.builder()
                                .id(message.getId())
                                .schema(schemaName)
                                .table(tableName)
                                .eventType(eventType)
                                .executeTime(entry.getHeader().getExecuteTime())
                                .build();
                        // 遍历RowDataList，并逐行调用Handler处理
                        for (DebeziumEntry.RowData rowData : rowChange.getRowDatasList()) {
                            this.handlerRowData(model, rowData, entryHandler, eventType);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
                }
            } else {
                log.info("当前操作类型为：{}", entryType);
            }
        }*/
    }
/*
    public void handlerRowData(DebeziumModel model, DebeziumEntry.RowChange rowChange, DebeziumEventHolder eventHolder, DebeziumEntry.EventType eventType) throws Exception {
        try {
            DebeziumContext.setModel(model);
            Method method = eventHolder.getMethod();
            ReflectionUtils.makeAccessible(method);
            Object[] args = GenericUtil.getInvokeArgs(method, model, rowChange, eventType);
            method.invoke(eventHolder.getTarget(), args);
        } finally {
            // 移除上下文
            DebeziumContext.removeModel();
        }
    }

    public void handlerRowData(DebeziumModel model, DebeziumEntry.RowData rowData, RecordChangeEventEntryHandler entryHandler, DebeziumEntry.EventType eventType) throws Exception {
        try {
            // 设置上下文
            DebeziumContext.setModel(model);
            // 逐行调用Handler处理
            rowDataHandler.handlerRowData(rowData, entryHandler, eventType);
        } finally {
            // 移除上下文
            DebeziumContext.removeModel();
        }
    }*/

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

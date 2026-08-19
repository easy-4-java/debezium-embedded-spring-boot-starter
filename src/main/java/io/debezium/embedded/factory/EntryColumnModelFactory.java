package io.debezium.embedded.factory;


import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.debezium.embedded.enums.TableNameEnum;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link IModelFactory} that materialises rows from a list of
 * {@link DebeziumEntry.Column} values, using MyBatis-Plus table metadata to
 * map column names to entity properties.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class EntryColumnModelFactory extends AbstractModelFactory<List<DebeziumEntry.Column>> {

    /** {@inheritDoc} */
    @Override
    /**
     * <p>New instance.</p>
     * @param entryHandler
     * @param columns
     * @return the result
     */
    public <R> R newInstance(RecordChangeEventEntryHandler entryHandler, List<DebeziumEntry.Column> columns) throws Exception {
        String debeziumTableName = HandlerUtil.getDebeziumTableNameCombination(entryHandler);
        if (TableNameEnum.ALL.name().toLowerCase().equals(debeziumTableName)) {
            Map<String, String> map = columns.stream().collect(Collectors.toMap(DebeziumEntry.Column::getName, DebeziumEntry.Column::getValue));
            return (R) map;
        }
        Class<R> entityClass = GenericUtil.getTableClass(entryHandler);
        if (entityClass != null) {
            return newInstance(entityClass, columns);
        }
        return null;
    }

    @Override
    /**
     * <p>New instance.</p>
     * @param entryHandler
     * @param columns
     * @param updateColumn
     * @return the result
     */
    public <R> R newInstance(RecordChangeEventEntryHandler entryHandler, List<DebeziumEntry.Column> columns, Set<String> updateColumn) throws Exception {
        String debeziumTableName = HandlerUtil.getDebeziumTableNameCombination(entryHandler);
        if (TableNameEnum.ALL.name().toLowerCase().equals(debeziumTableName)) {
            Map<String, String> map = columns.stream().filter(column -> updateColumn.contains(column.getName()))
                    .collect(Collectors.toMap(DebeziumEntry.Column::getName, DebeziumEntry.Column::getValue));
            return (R) map;
        }
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass != null) {
            // 获取 mybatis-plus 的注解信息
            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
            // 创建实体对象
            R object = BeanUtils.instantiateClass(tableClass);
            for (DebeziumEntry.Column column : columns) {
                if (updateColumn.contains(column.getName())) {
                    // 循环表数据
                    for (TableFieldInfo tableFieldInfo:  tableInfo.getFieldList()) {
                        String fieldName = tableFieldInfo.getProperty();
                        // 获取实体对象属性映射字段对应的值
                        if (StringUtils.equals(tableFieldInfo.getColumn(), column.getName())) {
                            PropertyUtils.setProperty(object, fieldName, column.getValue());
                            break;
                        }
                    }
                }
            }
            return object;
        }
        return null;
    }


    @Override
    <R> R newInstance(Class<R> rtClass, List<DebeziumEntry.Column> columns) throws Exception {
        // 如果列为空，返回null
        if(CollectionUtils.isEmpty(columns)){
            return null;
        }
        // 创建实体对象
        R object = BeanUtils.instantiateClass(rtClass);
        // 获取 mybatis-plus 的注解信息
        TableInfo tableInfo = TableInfoHelper.getTableInfo(rtClass);
        // 循环表数据
        for (TableFieldInfo tableFieldInfo:  tableInfo.getFieldList()) {
            String fieldName = tableFieldInfo.getProperty();
            for (DebeziumEntry.Column column : columns) {
                // 获取实体对象属性映射字段对应的值
                if (StringUtils.equals(tableFieldInfo.getColumn(), column.getName())) {
                    PropertyUtils.setProperty(object, fieldName, column.getValue());
                    break;
                }
            }
        }
        return object;
    }

}

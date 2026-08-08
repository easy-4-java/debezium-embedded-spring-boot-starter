package io.debezium.embedded.factory;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * {@link IModelFactory} that materialises rows from a {@code Map<String,String>}
 * of column-name to value, using MyBatis-Plus table metadata to map columns
 * to entity properties.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class MapColumnModelFactory extends AbstractModelFactory<Map<String, String>> {

    /**
     * Populates a new instance of {@code tableClass} from the supplied value map.
     *
     * @param tableClass the target row type
     * @param valueMap   column name to value mapping
     * @param <R>        the row model type
     * @return the materialised row model
     * @throws Exception if instantiation fails
     */
    @Override
    <R> R newInstance(Class<R> tableClass, Map<String, String> valueMap) throws Exception {
        R object = BeanUtils.instantiateClass(tableClass);
        // Read MyBatis-Plus table metadata
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
        // Iterate over mapped fields
        for (TableFieldInfo tableFieldInfo:  tableInfo.getFieldList()) {
            // Resolve the column value mapped to the entity property
            Object value = MapUtils.getObject(valueMap, tableFieldInfo.getColumn());
            PropertyUtils.setProperty(object, tableFieldInfo.getProperty(), value);
        }
        return object;
    }

}

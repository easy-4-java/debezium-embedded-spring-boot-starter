package io.debezium.embedded.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.debezium.embedded.model.DebeziumModel;

/**
 * debezium上下文
 */
public class DebeziumContext {

    private static TransmittableThreadLocal<DebeziumModel> threadLocal = new TransmittableThreadLocal<>();

    public static DebeziumModel getModel(){
        return threadLocal.get();
    }


    public static void setModel(DebeziumModel debeziumModel){
        threadLocal.set(debeziumModel);
    }


    public  static void removeModel(){
        threadLocal.remove();
    }
}

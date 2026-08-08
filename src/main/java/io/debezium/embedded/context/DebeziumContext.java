package io.debezium.embedded.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.debezium.embedded.model.DebeziumModel;

/**
 * Thread-local holder for the {@link DebeziumModel} currently being processed.
 * <p>Uses a {@link TransmittableThreadLocal} so that the model is correctly
 * propagated across thread boundaries when the executor hands work off to
 * other threads.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class DebeziumContext {

    private static TransmittableThreadLocal<DebeziumModel> threadLocal = new TransmittableThreadLocal<>();

    /**
     * @return the model bound to the current thread, or {@code null} if none.
     */
    public static DebeziumModel getModel(){
        return threadLocal.get();
    }


    /**
     * Binds the supplied model to the current thread.
     *
     * @param debeziumModel the model to bind
     */
    public static void setModel(DebeziumModel debeziumModel){
        threadLocal.set(debeziumModel);
    }


    /**
     * Removes the model bound to the current thread.
     */
    public  static void removeModel(){
        threadLocal.remove();
    }
}

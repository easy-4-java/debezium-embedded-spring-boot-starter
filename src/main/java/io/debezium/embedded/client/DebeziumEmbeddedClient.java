package io.debezium.embedded.client;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * Debezium Embedded 客户端
 * 支持多实例配置，整合了 DebeziumEmbeddedRunner 的功能
 */
@Slf4j
public class DebeziumEmbeddedClient extends AbstractDebeziumClient<RecordChangeEvent<SourceRecord>> {

    private DebeziumEmbeddedClient(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines,
                                   List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines,
                                   ThreadPoolTaskExecutor executor) {
        super(changeEventEngines, recordChangeEventEngines, executor);
    }

    public static final class Builder extends AbstractClientBuilder<DebeziumEmbeddedClient> {

        @Override
        public DebeziumEmbeddedClient build() {
            return new DebeziumEmbeddedClient(changeEventEngines, recordChangeEventEngines, debeziumTaskExecutor);
        }
    }
}

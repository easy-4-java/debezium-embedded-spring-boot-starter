package io.debezium.spring.boot;

import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import org.springframework.stereotype.Component;

@Component
public class DebeziumMessageRecordChangeEventEntryHandler implements RecordChangeEventEntryHandler<UserInfo> {

    @Override
    public void insert(UserInfo t) {
    }

    @Override
    public void update(UserInfo before, UserInfo after) {
    }

    @Override
    public void delete(UserInfo t) {
    }

}

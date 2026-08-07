/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.debezium.embedded.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Debezium Embedded 配置属性
 * @author ： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@ConfigurationProperties(DebeziumEmbeddedProperties.PREFIX)
@Data
public class DebeziumEmbeddedProperties {

	public static final String PREFIX = "debezium";

    /**
     * 配置信息
     */
    private List<Instance> instances = new ArrayList<>();

    @Data
    public static class Instance {

        /**
         * 订阅类型
        List<DebeziumEntry.EntryType> subscribeTypes = Collections.singletonList(DebeziumEntry.EntryType.ROWDATA);
         */

        /**
         * 订阅事件类型
         */
        EventType eventType = EventType.CHANGE_EVENT;

        /**
         * 异步引擎属性
         */
        DebeziumAsyncEngineProperties async = new DebeziumAsyncEngineProperties();

        /**
         * 连接器配置
         */
        DebeziumConnectorProperties connector = new DebeziumConnectorProperties();

        /**
         * 数据库历史记录配置
         */
        DebeziumSchemaHistoryProperties schemaHistory = new DebeziumSchemaHistoryProperties();

        /**
         * 偏移量存储配置
         */
        DebeziumOffsetStorageProperties offsetStorage = new DebeziumOffsetStorageProperties();

    }

    /**
     * <pre>
     ** 事件类型 *
     * </pre>
     */
    public enum EventType {
        CHANGE_EVENT,
        RECORD_CHANGE_EVENT;
    }

}

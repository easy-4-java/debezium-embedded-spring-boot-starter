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
 * Configuration properties for the Debezium Embedded engine.
 * <p>
 * Bound to the {@code debezium.*} namespace. Holds a list of
 * {@link Instance} definitions, each describing one independent Debezium
 * engine (connector + offset storage + schema history + async engine tuning).
 * </p>
 *
 * <p>Typical {@code application.yml} usage:</p>
 * <pre>{@code
 * debezium:
 *   instances:
 *     - connector:
 *         destination: order-pg
 *         type: POSTGRES
 *         host: db.local
 *         port: 5432
 *       event-type: CHANGE_EVENT
 * }</pre>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@ConfigurationProperties(DebeziumEmbeddedProperties.PREFIX)
@Data
public class DebeziumEmbeddedProperties {

    /**
     * Configuration prefix used by Spring Boot to bind properties.
     */
    public static final String PREFIX = "debezium";

    /**
     * Ordered list of Debezium engine instances to start. Each instance maps to
     * a separate {@link io.debezium.engine.DebeziumEngine} owned by the client.
     */
    private List<Instance> instances = new ArrayList<>();

    /**
     * Definition of a single Debezium engine instance.
     */
    @Data
    public static class Instance {

        /**
         * Type of change events this instance should emit.
         */
        EventType eventType = EventType.CHANGE_EVENT;

        /**
         * Asynchronous engine tuning (worker threads, shutdown timeout, ordering).
         */
        DebeziumAsyncEngineProperties async = new DebeziumAsyncEngineProperties();

        /**
         * Source database connector configuration.
         */
        DebeziumConnectorProperties connector = new DebeziumConnectorProperties();

        /**
         * Database schema history persistence configuration.
         */
        DebeziumSchemaHistoryProperties schemaHistory = new DebeziumSchemaHistoryProperties();

        /**
         * Offset storage (consumer position) configuration.
         */
        DebeziumOffsetStorageProperties offsetStorage = new DebeziumOffsetStorageProperties();

    }

    /**
     * Supported event formats emitted by the embedded engine.
     * <ul>
     *   <li>{@link #CHANGE_EVENT} — serialised JSON change events (high level consumer)</li>
     *   <li>{@link #RECORD_CHANGE_EVENT} — Connect {@code SourceRecord} change events (low level consumer)</li>
     * </ul>
     */
    public enum EventType {
        /** JSON-formatted change events produced via the {@code Json} format. */
        CHANGE_EVENT,
        /** Kafka Connect {@code SourceRecord} change events produced via the {@code Connect} format. */
        RECORD_CHANGE_EVENT;
    }

}

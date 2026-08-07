package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.configurer.connector.ConnectorType;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * MySQL 连接器配置示例
 */
@SpringBootApplication
public class MySqlConnectorExample {

    public static void main(String[] args) {
        SpringApplication.run(MySqlConnectorExample.class, args);
    }

    @Bean
    public DebeziumConnectorProperties mySqlConnectorProperties() {

        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        
        // 基础配置
        properties.setType(ConnectorType.MYSQL);
        properties.setDestination("mysql-connector");
        properties.setHost("localhost");
        properties.setPort(3306);
        properties.setUsername("root");
        properties.setPassword("password");
        properties.setServerId("1");
        properties.setServerName("mysql-server");
        
        // 数据库过滤
        properties.setDatabaseIncludeList("test_db,user_db");
        properties.setTableIncludeList("test_db.users,test_db.orders");
        properties.setDatabaseExcludeList("mysql,information_schema");
        
        // MySQL 特定配置
        DebeziumConnectorProperties.MySql mySql = properties.getMySql();
        
        // 快照配置
        mySql.setSnapshotMode("initial");
        mySql.setSnapshotLockingMode("minimal");
        mySql.setSnapshotNewTables(true);
        mySql.setSnapshotDelayMs(1000L);
        mySql.setSnapshotFetchSize(2048);
        
        // 性能配置
        mySql.setConnectTimeoutMs(30000);
        mySql.setPollIntervalMs(1000);
        mySql.setMaxQueueSize(16384);
        mySql.setMaxBatchSize(4096);
        mySql.setMaxQueueSizeInBytes(2147483648L);
        
        // GTID 配置
        mySql.setGtidSourceFilterDmlEvents(true);
        mySql.setGtidSourceIncludeDatabases("test_db,user_db");
        mySql.setGtidSourceExcludeDatabases("mysql,information_schema");
        
        // 连接配置
        mySql.setAllowPublicKeyRetrieval(true);
        mySql.setUseSSL(false);
        mySql.setAutoReconnect(true);
        mySql.setCharacterEncoding("utf8mb4");
        mySql.setServerTimezone("Asia/Shanghai");
        
        // 事件处理
        mySql.setIncludeQuery(true);
        mySql.setIncludeSchemaChanges(true);
        mySql.setProvideTransactionMetadata(true);
        
        // 增量快照
        mySql.setIncrementalSnapshotChunkSize(1024);
        mySql.setIncrementalSnapshotAllowSchemaChanges(true);
        mySql.setSignalDataCollection("test_db.debezium_signals");
        
        return properties;
    }
}

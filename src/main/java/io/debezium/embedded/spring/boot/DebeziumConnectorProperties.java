package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.connector.ConnectorType;
import lombok.Data;

/**
 * Debezium Connector 属性
 */
@Data
public class DebeziumConnectorProperties {

    /**
     * 连接器类型
     */
    private ConnectorType type = ConnectorType.MYSQL;

    /**
     * 连接器名称
     */
    private String destination;

    /**
     * 数据库主机
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 数据库名称（PostgreSQL 等使用）
     */
    private String databaseName;

    /**
     * 服务器 ID（MySQL 使用）
     */
    private String serverId;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 包含的数据库列表
     */
    private String databaseIncludeList;

    /**
     * 排除的数据库列表
     */
    private String databaseExcludeList;

    /**
     * 包含的表列表
     */
    private String tableIncludeList;

    /**
     * 排除的表列表
     */
    private String tableExcludeList;

    /**
     * 包含的 Schema 列表（PostgreSQL 等使用）
     */
    private String schemaIncludeList;

    /**
     * 排除的 Schema 列表
     */
    private String schemaExcludeList;

    /**
     * MySQL 特定配置
     */
    private MySql mySql = new MySql();

    /**
     * PostgreSQL 特定配置
     */
    private PostgreSql postgreSql = new PostgreSql();

    /**
     * MongoDB 特定配置
     */
    private MongoDb mongoDb = new MongoDb();

    /**
     * Oracle 特定配置
     */
    private Oracle oracle = new Oracle();

    /**
     * SQL Server 特定配置
     */
    private SqlServer sqlServer = new SqlServer();

    /**
     * Cassandra 特定配置
     */
    private Cassandra cassandra = new Cassandra();

    /**
     * Spanner 特定配置
     */
    private Spanner spanner = new Spanner();

    /**
     * 自定义连接器配置
     */
    private Custom custom = new Custom();

    @Data
    public static class MySql {
        // ==================== 快照配置 ====================
        /**
         * 快照模式
         * 可选值：initial, when_needed, never, schema_only, schema_only_recovery
         */
        private String snapshotMode = "initial";
        
        /**
         * 快照锁定模式
         * 可选值：minimal, extended, none
         */
        private String snapshotLockingMode = "minimal";
        
        /**
         * 快照新表
         * 是否对新创建的表进行快照
         */
        private Boolean snapshotNewTables = false;
        
        /**
         * 快照延迟（毫秒）
         * 快照开始前的延迟时间
         */
        private Long snapshotDelayMs = 0L;
        
        /**
         * 快照获取大小
         * 每次快照获取的行数
         */
        private Integer snapshotFetchSize = 1024;
        
        // ==================== 连接和性能配置 ====================
        /**
         * 连接超时时间（毫秒）
         */
        private Integer connectTimeoutMs = 30000;
        
        /**
         * 轮询间隔（毫秒）
         */
        private Integer pollIntervalMs = 1000;
        
        /**
         * 最大队列大小
         */
        private Integer maxQueueSize = 8192;
        
        /**
         * 最大批次大小
         */
        private Integer maxBatchSize = 2048;
        
        /**
         * 最小行数流结果
         */
        private Integer minRowCountToStreamResults = 1000;
        
        /**
         * 最大队列大小（字节）
         */
        private Long maxQueueSizeInBytes = 1073741824L; // 1GB
        
        // ==================== GTID 和复制配置 ====================
        /**
         * GTID 源过滤 DML 事件
         */
        private Boolean gtidSourceFilterDmlEvents = true;
        
        /**
         * GTID 源包含数据库
         * 逗号分隔的数据库列表
         */
        private String gtidSourceIncludeDatabases;
        
        /**
         * GTID 源排除数据库
         * 逗号分隔的数据库列表
         */
        private String gtidSourceExcludeDatabases;
        
        /**
         * GTID 源过滤 DDL 事件
         */
        private Boolean gtidSourceFilterDdlEvents = false;
        
        // ==================== 数据库连接配置 ====================
        /**
         * 允许公钥检索
         */
        private Boolean allowPublicKeyRetrieval = true;
        
        /**
         * 使用 SSL
         */
        private Boolean useSSL = false;
        
        /**
         * 自动重连
         */
        private Boolean autoReconnect = true;
        
        /**
         * 允许多查询
         */
        private Boolean allowMultiQueries = true;
        
        /**
         * 零日期时间行为
         * 可选值：convertToNull, exception, round
         */
        private String zeroDateTimeBehavior = "convertToNull";
        
        /**
         * 字符编码
         */
        private String characterEncoding = "utf8";
        
        /**
         * 使用 Unicode
         */
        private Boolean useUnicode = true;
        
        /**
         * 服务器时区
         */
        private String serverTimezone;
        
        /**
         * 连接时区
         */
        private String connectionTimeZone;
        
        // ==================== 事件处理配置 ====================
        /**
         * 删除时生成墓碑
         */
        private Boolean tombstonesOnDelete = false;
        
        /**
         * 包含查询
         */
        private Boolean includeQuery = false;
        
        /**
         * 包含 Schema 变更
         */
        private Boolean includeSchemaChanges = true;
        
        /**
         * 提供事务元数据
         */
        private Boolean provideTransactionMetadata = false;
        
        // ==================== 性能优化配置 ====================
        /**
         * 增量快照块大小
         */
        private Integer incrementalSnapshotChunkSize = 1024;
        
        /**
         * 增量快照允许 Schema 变更
         */
        private Boolean incrementalSnapshotAllowSchemaChanges = true;
        
        /**
         * 信号数据集合
         * 用于增量快照的信号表
         */
        private String signalDataCollection;
        
        // ==================== 安全配置 ====================
        /**
         * SSL 模式
         * 可选值：disabled, preferred, required, verify_ca, verify_identity
         */
        private String sslMode = "disabled";
        
        /**
         * SSL 信任库
         */
        private String sslTruststore;
        
        /**
         * SSL 信任库密码
         */
        private String sslTruststorePassword;
        
        /**
         * SSL 密钥库
         */
        private String sslKeystore;
        
        /**
         * SSL 密钥库密码
         */
        private String sslKeystorePassword;
        
        // ==================== 监控和调试配置 ====================
        /**
         * 数据库历史跳过不可解析的 DDL
         */
        private Boolean databaseHistorySkipUnparseableDdl = false;
        
        /**
         * 数据库历史仅存储监控表的 DDL
         */
        private Boolean databaseHistoryStoreOnlyMonitoredTablesDdl = false;
        
        /**
         * 数据库历史仅存储捕获表的 DDL
         */
        private Boolean databaseHistoryStoreOnlyCapturedTablesDdl = false;
        
        /**
         * 数据库历史 Kafka 恢复尝试次数
         */
        private Integer databaseHistoryKafkaRecoveryAttempts = 4;
        
        /**
         * 数据库历史 Kafka 恢复轮询间隔（毫秒）
         */
        private Integer databaseHistoryKafkaRecoveryPollIntervalMs = 100;
    }

    @Data
    public static class PostgreSql {
        /**
         * 插件名称
         */
        private String pluginName = "pgoutput";
        /**
         * 复制槽名称
         */
        private String slotName;
        /**
         * 发布名称
         */
        private String publicationName;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * SSL 模式
         */
        private String sslMode = "prefer";
        /**
         * SSL 证书
         */
        private String sslCert = "";
        /**
         * SSL 密钥
         */
        private String sslKey = "";
        /**
         * SSL 根证书
         */
        private String sslRootCert = "";
        /**
         * SSL 密码
         */
        private String sslPassword = "";
        /**
         * 删除时生成墓碑
         */
        private Boolean tombstonesOnDelete = false;
        /**
         * 包含查询
         */
        private Boolean includeQuery = false;
        /**
         * 轮询间隔（毫秒）
         */
        private Integer pollIntervalMs = 1000;
        /**
         * 最大队列大小
         */
        private Integer maxQueueSize = 8192;
        /**
         * 最大批次大小
         */
        private Integer maxBatchSize = 2048;
    }

    @Data
    public static class MongoDb {
        /**
         * 连接字符串
         */
        private String connectionString;
        /**
         * 数据库列表
         */
        private String databaseList;
        /**
         * 集合列表
         */
        private String collectionList;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 认证源
         */
        private String authSource = "admin";
        /**
         * 连接超时时间（毫秒）
         */
        private Integer connectTimeoutMs = 30000;
        /**
         * Socket 超时时间（毫秒）
         */
        private Integer socketTimeoutMs = 30000;
        /**
         * 服务器选择超时时间（毫秒）
         */
        private Integer serverSelectionTimeoutMs = 30000;
        /**
         * 最大连接池大小
         */
        private Integer maxConnectionPoolSize = 100;
        /**
         * 最小连接池大小
         */
        private Integer minConnectionPoolSize = 5;
        /**
         * 最大连接空闲时间（毫秒）
         */
        private Integer maxConnectionIdleTimeMs = 30000;
        /**
         * 最大连接生命周期（毫秒）
         */
        private Integer maxConnectionLifeTimeMs = 300000;
        /**
         * 删除时生成墓碑
         */
        private Boolean tombstonesOnDelete = false;
        /**
         * 包含查询
         */
        private Boolean includeQuery = false;
        /**
         * 字段重命名
         */
        private String fieldRenames = "";
        /**
         * 字段排除列表
         */
        private String fieldExcludeList = "";
        /**
         * 轮询间隔（毫秒）
         */
        private Integer pollIntervalMs = 1000;
        /**
         * 最大队列大小
         */
        private Integer maxQueueSize = 8192;
        /**
         * 最大批次大小
         */
        private Integer maxBatchSize = 2048;
        /**
         * 最大队列大小（字节）
         */
        private Long maxQueueSizeInBytes = 1073741824L;
    }

    @Data
    public static class Oracle {
        /**
         * 数据库名称
         */
        private String database;
        /**
         * PDB 名称
         */
        private String pdbName;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 日志挖掘策略
         */
        private String logMiningStrategy = "online_catalog";
    }

    @Data
    public static class SqlServer {
        /**
         * 数据库名称
         */
        private String database;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 快照隔离级别
         */
        private String snapshotIsolationMode = "snapshot";
    }

    @Data
    public static class Cassandra {
        /**
         * 连接字符串
         */
        private String connectionString;
        /**
         * 数据库列表（keyspace）
         */
        private String databaseList;
        /**
         * 表列表
         */
        private String tableList;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 连接超时时间（毫秒）
         */
        private Integer connectTimeoutMs = 30000;
        /**
         * 读取超时时间（毫秒）
         */
        private Integer readTimeoutMs = 30000;
        /**
         * 删除时生成墓碑
         */
        private Boolean tombstonesOnDelete = false;
        /**
         * 包含查询
         */
        private Boolean includeQuery = false;
        /**
         * 轮询间隔（毫秒）
         */
        private Integer pollIntervalMs = 1000;
        /**
         * 最大队列大小
         */
        private Integer maxQueueSize = 8192;
        /**
         * 最大批次大小
         */
        private Integer maxBatchSize = 2048;
    }

    @Data
    public static class Spanner {
        /**
         * 连接字符串
         */
        private String connectionString;
        /**
         * 数据库列表
         */
        private String databaseList;
        /**
         * 表列表
         */
        private String tableList;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 项目 ID
         */
        private String projectId;
        /**
         * 实例 ID
         */
        private String instanceId;
        /**
         * 数据库 ID
         */
        private String databaseId;
        /**
         * 删除时生成墓碑
         */
        private Boolean tombstonesOnDelete = false;
        /**
         * 包含查询
         */
        private Boolean includeQuery = false;
        /**
         * 轮询间隔（毫秒）
         */
        private Integer pollIntervalMs = 1000;
        /**
         * 最大队列大小
         */
        private Integer maxQueueSize = 8192;
        /**
         * 最大批次大小
         */
        private Integer maxBatchSize = 2048;
    }

    @Data
    public static class Custom {
        /**
         * 自定义连接器类名
         */
        private String connectorClass;
        /**
         * 自定义配置属性
         */
        private java.util.Map<String, String> props = new java.util.HashMap<>();
    }
}

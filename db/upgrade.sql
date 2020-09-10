DROP TABLE T_UPGRADE_SUBSCRIBE_INFO;
DROP TABLE T_UPGRADE_VERSION_INFO;
DROP TABLE T_UPGRADE_RECORD;
DROP TABLE T_UPGRADE_DOWNLOAD_ROUTE;

-- 订阅信息表
CREATE TABLE T_UPGRADE_SUBSCRIBE_INFO
(
    ID             INTEGER PRIMARY KEY autoincrement,
    CODE           VARCHAR(10),  -- 库点代码
    NAME           VARCHAR(70),  -- 库点名称
    APP_TYPE       VARCHAR(15),  -- 应用类型
    OS_TYPE        VARCHAR(15),  -- 操作系统类型
    IS_STAND_BY    CHAR(1),      -- 是否提供分发下载服务 1-是 0-否
    PUBLISH_URL    VARCAHR(100), -- 通知地址
    STATUS         CHAR(1),      -- 订阅状态 0-已注销 1-已订阅
    SUBSCRIBE_TIME VARCAHR(20)   -- 订阅时间
);


-- 版本信息表
CREATE TABLE T_UPGRADE_VERSION_INFO
(
    ID              INTEGER PRIMARY KEY autoincrement,
    APP_TYPE        VARCHAR(15),  -- 应用类型
    VERSION_NO      VARCHAR(15),  -- 版本号
    IS_FORCE_UPDATE CHAR(1),      -- 是否强制更新 1-是 0-否
    FILE_NAME       VARCAHR(50),  -- 文件名称
    FILE_PATH       VARCAHR(150), -- 文件路径
    FILE_SIZE       BIGINT,       -- 文件大小
    FILE_MD5        VARCAHR(40),  -- 文件MD5
    STATUS          CHAR(1),      -- 版本状态 1-正常 2-已注销
    ADD_SOURCE      CHAR(1),      -- 版本来源 1-本地发布 2-远程发布
    PUBLISH_DATE    VARCHAR(20),  -- 发布日期
    PUBLISH_TYPE    CHAR(1)       -- 发布类型 1-手动发布 2-自动发布
);


-- 更新记录表
CREATE TABLE T_UPGRADE_RECORD
(
    ID              INTEGER PRIMARY KEY autoincrement,
    SUBSCRIBE_ID    INTEGER,      -- 订阅信息ID
    CODE            VARCHAR(10),  -- 库点代码
    NAME            VARCHAR(70),  -- 库点名称
    PUBLISH_URL     VARCAHR(100), -- 通知地址
    VERSION_ID      INTEGER,      -- 版本ID
    APP_TYPE        VARCHAR(15),  -- 应用类型
    VERSION_NO      VARCHAR(15),  -- 版本号
    FILE_NAME       VARCAHR(50),  -- 文件名称
    FILE_SIZE       BIGINT,       -- 文件大小
    FILE_MD5        VARCAHR(40),  -- 文件MD5
    IS_FORCE_UPDATE CHAR(1),      -- 是否强制更新 1-是 0-否
    PUBLISH_TYPE    CHAR(1),      -- 发布类型 1-手动发布 2-自动发布
    NOTIFY_STATUS   CHAR(1),      -- 通知结果 1-成功 0-失败
    NOTIFY_COUNT    INTEGER,      -- 通知次数
    NOTIFY_TIME     INTEGER,      -- 最后通知时间
    DOWNLOAD_STATUS CHAR(1),      -- 下载状态 1-已下载 0-待下载
    DOWNLOAD_TIME   VARCHAR(20),  -- 下载成功时间
    UPGRADE_STATUS  CHAR(1),      -- 更新结果 1-成功 0-失败 2-已忽略
    UPGRADE_TIME    VARCHAR(20)   -- 更新成功时间
);

-- 下载路由表
CREATE TABLE T_UPGRADE_DOWNLOAD_ROUTE
(
    ID            INTEGER PRIMARY KEY autoincrement,
    HOST          VARCHAR(20),  -- 主机
    PORT          INTEGER,      -- 端口
    VERSION_NO    VARCHAR(15),  -- 版本号
    APP_TYPE      VARCHAR(15),  -- 应用类型
    FILE_NAME     VARCHAR(100), -- 下载文件名称
    PATH          VARCHAR(100), -- 路径
    DOWNLOAD_PATH VARCHAR(100)  -- 下载路径
);
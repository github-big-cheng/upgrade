DROP TABLE T_UPGRADE_SUBSCRIBE_INFO;
DROP TABLE T_UPGRADE_VERSION_INFO;
DROP TABLE T_UPGRADE_RECORD;

-- 订阅信息表
CREATE TABLE T_UPGRADE_SUBSCRIBE_INFO
(
    ID             INTEGER PRIMARY KEY autoincrement,
    CODE           VARCHAR(10),  -- 库点代码
    APP_TYPE       VARCHAR(15),  -- 应用类型
    OS_TYPE        VARCHAR(15),  -- 操作系统类型
    IS_STAND_BY    CHAR(1),      -- 是否提供分发下载服务 1-是 0-否
    PUBLISH_URL    VARCAHR(100), -- 通知地址
    STATUS         CHAR(1),      -- 订阅状态 0-未订阅 1-已订阅
    SUBSCRIBE_TIME VARCAHR(20)   -- 订阅时间
);


-- 版本信息表
CREATE TABLE T_UPGRADE_VERSION_INFO
(
    ID              INTEGER PRIMARY KEY autoincrement,
    APP_TYPE        VARCHAR(15),  -- 应用类型
    VERSION_NO      VARCHAR(15),  -- 版本号
    IS_FORCE_UPDATE CHAR(1),      -- 是否强制更新 1-是 0-否
    FILE_PATH       VARCAHR(250), -- 更新文件路径
    STATUS          CHAR(1),      -- 版本状态 1-正常 0-取消 2-已过期
    PUBLISH_DATE    VARCHAR(20)   -- 发布日期
);


-- 更新记录表
CREATE TABLE T_UPGRADE_RECORD
(
    ID             INTEGER PRIMARY KEY autoincrement,
    SUBSCRIBE_ID   INTEGER, -- 订阅信息ID
    VERSION_ID     INTEGER, -- 版本号
    NOTIFY_STATUS  CHAR(1), -- 通知结果 1-成功 0-失败
    NOTIFY_STATUS  INTEGER, -- 通知次数
    UPGRADE_STATUS CHAR(1)  -- 更新结果 1-成功 0-失败
);
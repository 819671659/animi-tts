-- 1. 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 创建邮箱验证码表
CREATE TABLE IF NOT EXISTS email_code_verify (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    type TINYINT(1) COMMENT '类型（1-注册，2-找回密码）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码表';

-- 3. 修改 tts_record 表，增加 user_id
ALTER TABLE tts_record ADD COLUMN user_id BIGINT COMMENT '所属用户 ID' AFTER id;
ALTER TABLE tts_record ADD INDEX idx_user_id (user_id);

-- 4. 修改 custom_voice 表
ALTER TABLE custom_voice ADD COLUMN user_id BIGINT COMMENT '所属用户 ID' AFTER id;
ALTER TABLE custom_voice ADD COLUMN is_public TINYINT(1) DEFAULT 0 COMMENT '是否私有（0-私有，1-公开）' AFTER user_id;
ALTER TABLE custom_voice ADD INDEX idx_user_id (user_id);
ALTER TABLE custom_voice ADD INDEX idx_is_public (is_public);

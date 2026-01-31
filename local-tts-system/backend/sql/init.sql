-- 创建数据库
CREATE DATABASE IF NOT EXISTS local_tts_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE local_tts_db;

-- 表一：TTS 生成记录表
CREATE TABLE IF NOT EXISTS tts_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    text_content TEXT NOT NULL COMMENT '原始文本内容',
    voice_name VARCHAR(50) NOT NULL COMMENT '语音角色名称',
    rate VARCHAR(20) DEFAULT '+0%' COMMENT '语速',
    pitch VARCHAR(20) DEFAULT '+0Hz' COMMENT '音调',
    audio_url VARCHAR(255) NOT NULL COMMENT '音频文件路径',
    file_size BIGINT COMMENT '文件大小（字节）',
    duration INT COMMENT '音频时长（秒）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除（0-正常，1-删除）',
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TTS 生成记录表';

-- 表二：语音配置预设表
CREATE TABLE IF NOT EXISTS voice_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    config_name VARCHAR(50) NOT NULL UNIQUE COMMENT '配置名称',
    voice_name VARCHAR(50) NOT NULL COMMENT '语音角色名称',
    rate VARCHAR(20) DEFAULT '+0%' COMMENT '语速',
    pitch VARCHAR(20) DEFAULT '+0Hz' COMMENT '音调',
    description VARCHAR(200) COMMENT '配置描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='语音配置预设表';

-- 表三：自定义音色表
CREATE TABLE IF NOT EXISTS custom_voice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    voice_name VARCHAR(100) NOT NULL UNIQUE COMMENT '音色名称（用户自定义）',
    sample_audio_url VARCHAR(255) NOT NULL COMMENT '原始音频样本路径',
    model_path VARCHAR(255) COMMENT '训练后的模型文件路径',
    training_status TINYINT(1) DEFAULT 0 COMMENT '训练状态（0-待训练，1-训练中，2-已完成，3-失败）',
    sample_duration INT COMMENT '样本音频时长（秒）',
    sample_text TEXT COMMENT '样本音频对应的文本（可选，用于优化训练）',
    description VARCHAR(200) COMMENT '音色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE INDEX uk_voice_name (voice_name),
    INDEX idx_training_status (training_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义音色表';

-- 插入预设语音配置（基于官方支持的稳定音色）
INSERT INTO voice_config (config_name, voice_name, rate, pitch, description) VALUES
-- 女声系列
('萝莉音', 'zh-CN-XiaoyiNeural', '+10%', '+15Hz', '可爱清脆的童声（由晓伊调制）'),
('少女音', 'zh-CN-XiaoyiNeural', '+0%', '+5Hz', '年轻清新的少女声音（晓伊）'),
('御姐音', 'zh-CN-XiaoxiaoNeural', '+0%', '-2Hz', '温柔成熟的女性声音（晓晓）'),
('专业女声', 'zh-CN-XiaoxiaoNeural', '+0%', '+0Hz', '标准成熟女声（晓晓）'),

-- 男声系列
('少年音', 'zh-CN-YunxiaNeural', '+5%', '+10Hz', '朝气蓬勃的少年男声（云夏）'),
('大叔音', 'zh-CN-YunyangNeural', '-5%', '-10Hz', '沉稳磁性的成熟男声（云扬）'),
('磁性男声', 'zh-CN-YunxiNeural', '+0%', '-5Hz', '低沉有磁性的男声（云希）'),

-- 特殊音色
('老奶奶音', 'zh-CN-XiaoxiaoNeural', '-15%', '-12Hz', '慈祥年长的女性声音（调制）'),
('新闻播音', 'zh-CN-YunjianNeural', '+0%', '+0Hz', '专业新闻播报男声（云健）'),

-- 方言特色
('粤语女声', 'zh-HK-HiuMaanNeural', '+0%', '+0Hz', '粤语女性声音（晓曼）'),
('粤语男声', 'zh-HK-WanLungNeural', '+0%', '+0Hz', '粤语男性声音（云龙）'),
('台湾女声', 'zh-TW-HsiaoChenNeural', '+0%', '+0Hz', '台湾口音女性（晓臻）'),
('台湾男声', 'zh-TW-YunJheNeural', '+0%', '+0Hz', '台湾口音男性（云哲）');

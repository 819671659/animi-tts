# 本地文字配音系统 - 后端

## 项目简介

本项目是一个完全本地化、离线运行的文字转语音（TTS）系统后端服务，基于 Spring Boot + MyBatis-Plus + MySQL 构建，支持预设语音配音和自定义音色功能。

## 技术栈

- **框架**：Spring Boot 2.7.18
- **ORM**：MyBatis-Plus 3.5.5
- **数据库**：MySQL 8.0
- **工具库**：Hutool 5.8.25、Lombok
- **TTS 引擎**：Edge-TTS（预设语音）+ Coqui TTS（自定义音色，待扩展）

## 核心功能

### ✅ 已实现功能

1. **文字转语音**（TTS）
   - 支持微软 Edge 内置的多种中文神经语音
   - 可调节语速、音调
   - 生成 MP3 格式音频文件

2. **预设语音管理**
   - 提供 5 种预设中文语音（晓晓、云希、晓伊、云扬、晓梦）
   - 支持查询可用语音列表

3. **历史记录**
   - 保存每次配音记录（文本、语音参数、音频文件）
   - 支持分页查询历史记录
   - 支持删除历史记录

4. **音频文件下载**
   - 提供音频文件下载接口
   - 支持浏览器在线播放

5. **自定义音色上传 🔧**
   - 支持上传音色样本音频
   - 数据库存储音色信息

### 🚧 待扩展功能（后续开发）

1. **声音克隆训练**
   - 集成 Coqui TTS 或 RVC 声音克隆引擎
   - 实现异步训练任务队列
   - 支持使用自定义音色生成语音

2. **功能增强**
   - 批量文本转换
   - 音频格式转换（WAV、OGG）
   - 音频后处理（降噪、音量标准化）
   - SSML 标记语言支持

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+ / 8.0+
- Python 3.8+（用于调用 edge-tts）

### 安装步骤

1. **安装 Python 依赖**
   ```bash
   pip install edge-tts
   ```

2. **初始化数据库**
   ```bash
   # 在 MySQL 中执行 sql/init.sql 脚本
   mysql -u root -p < sql/init.sql
   ```

3. **配置数据库**
   修改 `src/main/resources/application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/local_tts_db
       username: root
       password: your_password_here  # 修改为实际密码
   ```

4. **编译运行**
   ```bash
   # 使用 Maven 编译
   mvn clean package
   
   # 运行项目
   java -jar target/local-tts-system-1.0.0.jar
   
   # 或使用 Maven 直接运行
   mvn spring-boot:run
   ```

5. **访问服务**
   - 服务地址：http://localhost:2211
   - 接口文档：查看下方 API 接口列表

## API 接口

### TTS 相关接口

| 接口路径 | 方法 | 功能 | 请求参数 |
|---------|------|------|----------|
| `/api/tts/generate` | POST | 生成语音 | `TtsRequestDTO` |
| `/api/tts/voices` | GET | 获取语音列表 | - |
| `/api/tts/download/{filename}` | GET | 下载音频文件 | filename |
| `/api/tts/history` | GET | 查询历史记录 | page, size |
| `/api/tts/delete/{id}` | DELETE | 删除历史记录 | id |

### 自定义音色接口

| 接口路径 | 方法 | 功能 | 请求参数 |
|---------|------|------|----------|
| `/api/voice/upload` | POST | 上传音色样本 | voiceName, audioFile |
| `/api/voice/list` | GET | 获取自定义音色列表 | page, size |
| `/api/voice/train/{id}` | POST | 训练音色模型 🚧 | id |
| `/api/voice/delete/{id}` | DELETE | 删除自定义音色 | id |

### 请求示例

**生成语音**
```bash
curl -X POST http://localhost:2211/api/tts/generate \
  -H "Content-Type: application/json" \
  -d '{
    "text": "你好，这是测试文本",
    "voiceName": "zh-CN-XiaoxiaoNeural",
    "rate": "+0%",
    "pitch": "+0Hz"
  }'
```

**上传音色样本**
```bash
curl -X POST http://localhost:2211/api/voice/upload \
  -F "voiceName=我的声音" \
  -F "audioFile=@sample.mp3" \
  -F "description=个人音色样本"
```

## 项目结构

```
backend/
├── src/main/java/com/tts/
│   ├── TtsApplication.java          # 启动类
│   ├── config/                      # 配置类
│   │   ├── CorsConfig.java         # 跨域配置
│   │   └── MyBatisPlusConfig.java  # MyBatis-Plus 配置
│   ├── controller/                  # 控制器
│   │   ├── TtsController.java      # TTS 控制器
│   │   └── CustomVoiceController.java  # 自定义音色控制器
│   ├── service/                     # 服务层
│   │   ├── ITtsService.java
│   │   ├── IVoiceConfigService.java
│   │   ├── ICustomVoiceService.java
│   │   └── impl/                   # 服务实现
│   ├── mapper/                      # 数据访问层
│   │   ├── TtsRecordMapper.java
│   │   ├── VoiceConfigMapper.java
│   │   └── CustomVoiceMapper.java
│   ├── entity/                      # 实体类
│   │   ├── TtsRecord.java
│   │   ├── VoiceConfig.java
│   │   └── CustomVoice.java
│   ├── dto/                         # 数据传输对象
│   │   ├── TtsRequestDTO.java
│   │   └── TtsResponseDTO.java
│   ├── common/                      # 公共类
│   │   ├── Result.java             # 统一响应体
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   └── utils/                       # 工具类
│       └── TtsEngineUtil.java      # TTS 引擎工具
├── src/main/resources/
│   ├── application.yml              # 配置文件
│   └── scripts/                     # Python 脚本
│       └── edge_tts_generator.py   # Edge TTS 调用脚本
└── sql/
    └── init.sql                    # 数据库初始化脚本
```

## 注意事项

1. **数据库密码**：请在 `application.yml` 中修改数据库密码
2. **Python 环境**：确保系统已安装 Python 3.8+ 和 edge-tts 包
3. **音频文件存储**：生成的音频文件默认存储在 `src/main/resources/static/audios/` 目录
4. **自定义音色训练**：当前仅支持上传样本，训练功能待后续扩展

## 常见问题

**Q: 启动时提示 "Python 脚本执行失败"？**
A: 请检查：
1. 是否已安装 Python 3.8+
2. 是否已安装 edge-tts（`pip install edge-tts`）
3. `application.yml` 中的 `python-path` 配置是否正确

**Q: 生成语音时报错 "Training failed"？**
A: Edge-TTS 首次运行需要联网下载语音模型，请确保网络连接正常。

**Q: 自定义音色训练功能何时上线？**
A: 该功能需要集成 Coqui TTS 或 RVC 引擎，计划在后续版本实现。目前可先使用预设语音。

## 开发计划

- [x] 基础 TTS 功能（edge-tts）
- [x] 预设语音管理
- [x] 历史记录功能
- [x] 自定义音色上传
- [ ] 声音克隆训练（Coqui TTS 集成）
- [ ] 异步任务队列
- [ ] 音频格式转换
- [ ] 批量处理功能

## 许可证

MIT License

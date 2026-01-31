# 本地文字配音系统

一个完全本地化、离线运行的文字转语音（TTS）系统，采用前后端分离架构，支持预设语音配音和自定义音色功能。

## 项目特性

- ✅ **完全离线**：基于 edge-tts 引擎，无需调用外部 AI 接口
- 🎙️ **高质量语音**：支持微软 Edge 浏览器内置的多种中文神经语音
- ⚙️ **灵活配置**：可调节语速、音调、选择不同语音角色
- 📦 **前后端分离**：Vue 3 前端 + Spring Boot 后端，易于维护和扩展
- 🔧 **自定义音色**：支持上传音色样本（声音克隆功能待扩展）

## 技术栈

### 前端
- Vue 3 + Vite + TypeScript
- Pinia（状态管理）
- Element Plus（UI组件库）（待实现）

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Edge-TTS（预设语音）
- Coqui TTS（自定义音色，待扩展）

## 快速开始

### 环境要求

- **后端**：
  - JDK 8+
  - Maven 3.6+
  - MySQL 5.7+ / 8.0+
  - Python 3.8+

- **前端**：
  - Node.js 18+
  - npm 或 pnpm

### 安装步骤

**1. 安装 Python 依赖**
```bash
pip install edge-tts
```

**2. 初始化数据库**
```bash
# 在 MySQL 中执行初始化脚本
mysql -u root -p < backend/sql/init.sql
```

**3. 启动后端**
```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库密码
mvn spring-boot:run
# 后端将运行在 http://localhost:2211
```

**4. 启动前端（待实现）**
```bash
cd frontend
npm install
npm run dev
```

## 项目结构

```
local-tts-system/
├── backend/                # 后端服务（Spring Boot）
│   ├── src/
│   ├── pom.xml
│   ├── sql/init.sql       # 数据库初始化脚本
│   └── README.md
│
├── frontend/               # 前端应用（Vue 3）（待实现）
│   └── ...
│
└──README.md               # 项目总文档
```

## 核心功能

### ✅ 已实现

- [x] 文字转语音（edge-tts）
- [x] 预设语音管理（5种中文语音）
- [x] 历史记录功能
- [x] 音频文件下载
- [x] 自定义音色样本上传

### 🚧 开发中

- [ ] 前端界面（Vue 3）
- [ ] 声音克隆训练（Coqui TTS）
- [ ] 批量文本处理
- [ ] 音频格式转换

## 使用说明

详细使用说明请查看：
- [后端使用文档](backend/README.md)
- [前端使用文档](frontend/README.md)（待实现）

## 开发计划

1. **第一阶段（已完成）** ✅
   - 后端基础功能实现
   - 预设语音配音
   - 历史记录管理

2. **第二阶段（进行中）** 🚧
   - 前端界面开发
   - 自定义音色UI

3. **第三阶段（计划中）** 📋
   - 声音克隆训练集成
   - 功能增强（批量处理、格式转换等）

## 常见问题

**Q: 如何更换数据库密码？**
A: 修改 `backend/src/main/resources/application.yml` 中的 `spring.datasource.password`

**Q: 自定义音色训练功能何时上线？**
A: 该功能需要集成 Coqui TTS 引擎，计划在后续版本实现。目前可先使用预设语音。

**Q: 是否完全离线？**
A: edge-tts 首次运行需联网下载语音模型，后续可离线使用（取决于缓存策略）。如需 100% 离线，可选择集成 Coqui TTS。

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提交 Issue！

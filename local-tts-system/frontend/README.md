# 本地文字配音系统 - 前端

## 项目简介

基于 Vue 3 + Vite + TypeScript 构建的现代化前端应用，提供直观的文字转语音界面。

## 技术栈

- **框架**：Vue 3 (Composition API)
- **构建工具**:vite 5.x
- **语言**：TypeScript
- **状态管理**：Pinia
- **HTTP 客户端**：Axios

## 快速开始

### 安装依赖

```bash
npm install
# 或
pnpm install
```

### 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:5173

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
frontend/
├── src/
│   ├── api/                 # API 接口封装
│   │   └── tts.ts          # TTS 相关接口
│   ├── components/          # Vue 组件
│   │   ├── TextEditor.vue  # 文本编辑器
│   │   ├── VoiceSettings.vue  # 语音设置
│   │   └── AudioPlayer.vue    # 音频播放器
│   ├── stores/              # Pinia 状态管理
│   │   └── tts.ts          # TTS 状态
│   ├── types/               # TypeScript 类型定义
│   │   └── tts.d.ts        # TTS 类型
│   ├── utils/               # 工具函数
│   │   └── request.ts      # Axios 封装
│   ├── App.vue              # 根组件
│   ├── main.ts              # 应用入口
│   └── style.css            # 全局样式
├── index.html               # HTML 模板
├── vite.config.ts           # Vite 配置
├── tsconfig.json            # TypeScript 配置
└── package.json             # 项目配置
```

## 核心功能

### ✅ 已实现

- [x] 文本输入与编辑（字数统计）
- [x] 语音参数配置（语速、音调、语音选择）
- [x] 音频生成（调用后端 API）
- [x] 音频播放与下载
- [x] 错误处理与提示
- [x] 响应式设计

### 🚧 开发中

- [ ] 历史记录列表
- [ ] 自定义音色管理界面
- [ ] 批量文本处理
- [ ] 音频波形可视化

## API 代理配置

开发环境下，所有 `/api` 请求会被代理到后端服务（默认 `http://localhost:2211`）。

修改代理地址请编辑 `vite.config.ts`：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url',
      changeOrigin: true
    }
  }
}
```

## 注意事项

1. **后端服务**：启动前端前请确保后端服务已运行在 `http://localhost:2211`
2. **浏览器兼容性**：推荐使用 Chrome / Edge / Firefox 最新版本
3. **音频播放**：某些浏览器可能需要用户交互才能自动播放音频

## 开发建议

- 使用 VSCode + Volar 插件获得最佳开发体验
- 启用 TypeScript 类型检查
- 遵循 Vue 3 Composition API 最佳实践

## 许可证

MIT License

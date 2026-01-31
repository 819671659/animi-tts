# 🚀 本地文字配音系统 - 全平台保姆级部署教程

本教程旨在指导您从零开始，在 **Windows** 或 **Linux (Ubuntu/Debian)** 系统上完成本项目的完整部署。

---

## 📋 目录
1. [环境要求概览](#1-环境要求概览)
2. [后端环境配置 (Java & MySQL)](#2-后端环境配置-java--mysql)
3. [Python 语音引擎配置 (Edge-TTS & XTTS)](#3-python-语音引擎配置-edge-tts--xtts)
4. [前端环境配置 (Node.js)](#4-前端环境配置-node-js)
5. [项目启动流程](#5-项目启动流程)
6. [常见问题排查 (重要)](#6-常见问题排查-重要)

---

## 1. 环境要求概览

| 组件 | 最低版本 | 作用 |
| :--- | :--- | :--- |
| **JDK** | 1.8 | 后端 Spring Boot 运行环境 |
| **Maven** | 3.6+ | 后端依赖管理与打包 |
| **MySQL** | 8.0 | 存储历史记录、音色配置及系统数据 |
| **Python** | 3.8+ | 核心语音引擎（Edge-TTS & Coqui-TTS） |
| **Node.js** | 18.x+ | 前端 Vue 3 运行环境 |

---

## 2. 后端环境配置 (Java & MySQL)

### 2.1 JDK 8 安装与检查
- **Windows**: 下载 [Zulu JDK 8](https://www.azul.com/downloads/?package=jdk#zulu) 或 Oracle JDK。安装后需将 `bin` 目录添加到系统变量 `PATH` 中。
- **Linux (Ubuntu)**: 
  ```bash
  sudo apt update
  sudo apt install openjdk-8-jdk -y
  ```
- **检查命令**: `java -version`
  - *预期输出*: `openjdk version "1.8.x"`

### 2.2 Maven 安装与检查
- **Windows**: 下载 [Apache Maven](https://maven.apache.org/download.cgi)，解压并配置 `PATH`。
- **Linux (Ubuntu)**: 
  ```bash
  sudo apt install maven -y
  ```
- **检查命令**: `mvn -v`
  - *预期输出*: `Apache Maven 3.6.x`

### 2.3 MySQL 8.0 数据库初始化
1.  **安装**: 确保 MySQL 服务已运行。
2.  **创建数据库**:
    ```sql
    CREATE DATABASE local_tts_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  **执行 SQL**: 定位到 `local-tts-system/backend/sql/init.sql`，将其导入到 `local_tts_db` 中。
    - *命令*: `mysql -u root -p local_tts_db < ./local-tts-system/backend/sql/init.sql`

---

## 3. Python 语音引擎配置 (Edge-TTS & XTTS)

本项目使用 Python 构建高性能语音生成模块，**必须确保以下依赖正确安装**。

### 3.1 基础环境安装
- **Windows**: 前往 [Python 官网](https://www.python.org/) 下载 3.8+，安装时勾选 **"Add Python to PATH"**。
- **Linux**: `sudo apt install python3 python3-pip python3-venv -y`

### 3.2 依赖包安装 (关键)
建议在项目根目录下创建虚拟环境，避免污染全局：
```bash
# 创建虚拟环境
python -m venv venv

# 激活虚拟环境 (Windows)
.\venv\Scripts\activate
# 激活虚拟环境 (Linux)
source venv/bin/activate

# 安装依赖
pip install --upgrade pip
pip install edge-tts
# 如果需要语音克隆功能 (Coqui TTS)
pip install TTS
```

- **检查命令**: 
  - `pip show edge-tts`
  - `pip show TTS`（如需克隆功能）

---

## 4. 前端环境配置 (Node.js)

### 4.1 Node.js 安装
- **Windows/Mac**: 从 [Node.js 官网](https://nodejs.org/zh-cn) 下载 LTS 版本 (v18+)。
- **Linux (Ubuntu)**:
  ```bash
  curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
  sudo apt install -y nodejs
  ```
- **检查命令**: `node -v`

---

## 5. 项目启动流程

### 5.1 后端启动 (Port: 2211)
1.  修改配置文件：打开 `local-tts-system/backend/src/main/resources/application.yml`。
    -   `spring.datasource.password`: 修改为您的数据库密码。
    -   `python-path`: 如果使用了虚拟环境，请指向 `venv` 目录下的 python 可执行文件路径。
2.  运行启动：
    ```bash
    cd local-tts-system/backend
    mvn clean spring-boot:run
    ```

### 5.2 前端启动 (Port: 5173)
1.  安装依赖：
    ```bash
    cd local-tts-system/frontend
    npm install
    ```
2.  启动：
    ```bash
    npm run dev
    ```

---

## 6. 常见问题排查 (重要)

-   **Q: Python 脚本执行失败，提示库未找到？**
    A: 确保在 `application.yml` 中配置的 `python-path` 是您安装了依赖的那个 Python（如虚拟环境的路径）。
-   **Q: 语音克隆功能报错 "Missing TTS dependency"？**
    A: XTTS 引擎需要 `TTS` 包及其关联系统库（Linux 下需要 `ffmpeg`, `libsndfile1`）。
-   **Q: 首次运行 Edge-TTS 没反应？**
    A: Edge-TTS 需要联网下载模型缓存（约 10MB），若网络环境较差，可能会超时或卡顿。
-   **Q: 文件保存到哪里了？**
    A: 系统采用 `tts-storage` 目录作为持久化存储，音频文件和克隆样本都在此目录下。请确保该目录有 **写入权限**。
-   **Q: 数据库连接失败？**
    A: 请检查 MySQL 服务是否启动，以及 `application.yml` 中的驱动类名是否与您的 MySQL 版本匹配（默认已针对 8.0 优化）。

---

祝您配音愉快！如有进阶需求，请参考各模块目录下的 `README.md`。

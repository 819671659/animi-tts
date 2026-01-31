# 本地文字配音系统 - 部署指南 (DEPLOYMENT.md)

本指南旨在帮助您在 Windows 或 Linux 系统上从零开始部署本项目。本项目采用前后端分离架构，包含 Java (Spring Boot)、Python (Edge-TTS/Coqui-TTS) 和 Node.js (Vue 3)。

---

## 1. 基础环境检查与安装

在开始之前，请确保您的系统中安装了以下基础组件。

### 1.1 JDK 8 (后端运行环境)
*   **安装命令 (Ubuntu/Debian):** `sudo apt update && sudo apt install openjdk-8-jdk`
*   **安装命令 (Windows):** 请前往 [Oracle 官网](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) 下载并安装。
*   **检查命令:** `java -version`
    *   *预期输出:* 包含 `openjdk version "1.8.x"` 或 `java version "1.8.x"`。

### 1.2 Maven 3.6+ (后端构建工具)
*   **安装命令 (Ubuntu/Debian):** `sudo apt install maven`
*   **安装命令 (Windows):** 下载 [Apache Maven](https://maven.apache.org/download.cgi) 并配置环境变量。
*   **检查命令:** `mvn -v`
    *   *预期输出:* 包含 `Apache Maven 3.6.x` 或更高版本。

### 1.3 MySQL 8.0 (数据库)
*   **安装命令 (Ubuntu/Debian):** `sudo apt install mysql-server`
*   **安装命令 (Windows):** 下载并运行 [MySQL Installer](https://dev.mysql.com/downloads/installer/)。
*   **检查命令:** `mysql --version`
    *   *预期输出:* 包含 `mysql  Ver 8.0.x`。

### 1.4 Python 3.8+ (语音引擎支持)
*   **安装命令 (Ubuntu/Debian):** `sudo apt install python3 python3-pip`
*   **安装命令 (Windows):** 前往 [Python 官网](https://www.python.org/downloads/) 下载，**确保勾选 "Add Python to PATH"**。
*   **检查命令:** `python --version` (Windows) 或 `python3 --version` (Linux)
    *   *预期输出:* `Python 3.8` 或更高版本。

### 1.5 Node.js 18+ (前端运行环境)
*   **安装命令 (Ubuntu/Debian):**
    ```bash
    curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
    sudo apt-get install -y nodejs
    ```
*   **安装命令 (Windows):** 从 [Node.js 官网](https://nodejs.org/) 下载 LTS 版本。
*   **检查命令:** `node -v`
    *   *预期输出:* `v18.x.x` 或更高。

---

## 2. 数据库配置

1.  **登录数据库:** `mysql -u root -p`
2.  **创建数据库:** `CREATE DATABASE tts_system DEFAULT CHARACTER SET utf8mb4;`
3.  **运行脚本:**
    *   定位到项目中的 `backend/sql/init.sql`。
    *   在 MySQL 中执行：`use tts_system; source /路径/to/backend/sql/init.sql;`

---

## 3. 后端部署 (Spring Boot)

1.  **安装 Python 依赖:**
    ```bash
    # 安装 edge-tts
    pip install edge-tts
    ```
2.  **修改配置文件:**
    *   打开 `backend/src/main/resources/application.yml`。
    *   修改 `spring.datasource.password` 为您的 MySQL 密码。
3.  **编译并运行:**
    ```bash
    cd backend
    mvn clean spring-boot:run
    ```
    *   *后端地址:* `http://localhost:2211`

---

## 4. 前端部署 (Vue 3)

1.  **安装依赖:**
    ```bash
    cd frontend
    npm install
    ```
2.  **启动开发服务器:**
    ```bash
    npm run dev
    ```
    *   *前端地址:* `http://localhost:5173` (具体以终端输出为准)

---

## 5. 常见问题 (FAQ)

*   **端口占用:** 如果 2211 端口被占用，请在 `application.yml` 中修改 `server.port`。
*   **Python 命令未找到:** Linux 用户请尝试使用 `python3` 和 `pip3`。
*   **Edge-TTS 联网:** 首次运行可能需要联网下载语音模型缓存。

祝您使用愉快！

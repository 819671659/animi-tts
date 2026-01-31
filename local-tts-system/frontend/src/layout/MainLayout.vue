<template>
  <el-container class="main-layout">
    <el-aside width="240px" class="aside">
      <div class="logo floating">
        <span class="text-gradient">Anime TTS</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
      >
        <el-menu-item index="/">
          <el-icon><Mic /></el-icon>
          <span>语音生成</span>
        </el-menu-item>
        <el-menu-item index="/history">
          <el-icon><List /></el-icon>
          <span>生成记录</span>
        </el-menu-item>
        <el-menu-item index="/voices">
          <el-icon><User /></el-icon>
          <span>我的音色</span>
        </el-menu-item>
        <el-menu-item index="/square">
          <el-icon><Globe /></el-icon>
          <span>音色广场</span>
        </el-menu-item>
        <el-divider />
        <el-menu-item index="/profile">
          <el-icon><Settings /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header glass-card">
        <div class="header-content">
          <div class="breadcrumb">{{ currentTitle }}</div>
          <div class="user-info" v-if="userStore.userInfo">
            <el-avatar :size="32" :src="userStore.userInfo.avatar" />
            <span class="username">{{ userStore.userInfo.username }}</span>
            <el-button link type="danger" @click="handleLogout" class="logout-btn">
              <el-icon class="el-icon--left"><LogOut /></el-icon> 退出
            </el-button>
          </div>
          <div class="user-info" v-else>
            <el-button link type="danger" @click="handleLogout">
              <el-icon class="el-icon--left"><LogOut /></el-icon> 强制退出
            </el-button>
          </div>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { Mic, List, User, Globe, Settings, LogOut } from 'lucide-vue-next'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

onMounted(() => {
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.fetchUserInfo()
  }
})

const currentTitle = computed(() => {
  const titles: Record<string, string> = {
    '/': '语音生成',
    '/history': '生成记录',
    '/voices': '我的音色',
    '/square': '音色广场',
    '/profile': '个人中心'
  }
  return titles[route.path] || ''
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.aside {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  border-right: 1px solid var(--glass-border);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 30px;
}

.text-gradient {
  background: linear-gradient(to right, var(--primary-color), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.menu {
  border-right: none;
  background: transparent;
}

.el-menu-item {
  border-radius: 12px;
  margin-bottom: 8px;
  color: var(--text-color);
}

.el-menu-item.is-active {
  background: linear-gradient(to right, rgba(255, 133, 161, 0.1), rgba(192, 132, 252, 0.1)) !important;
  color: var(--primary-color) !important;
  font-weight: 600;
}

.header {
  margin: 20px;
  height: 60px;
  display: flex;
  align-items: center;
  z-index: 10;
}

.header-content {
  width: 100%;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breadcrumb {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 500;
}

.main {
  padding: 0 20px 20px 20px;
}

/* 路由动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>

<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="glass-card user-card">
          <div class="avatar-wrapper floating">
            <el-avatar :size="100" :src="userStore.userInfo?.avatar || defaultAvatar" />
          </div>
          <h3 class="username">{{ userStore.userInfo?.username }}</h3>
          <p class="email">{{ userStore.userInfo?.email }}</p>
          
          <el-divider />
          
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-num">{{ stats.recordCount }}</span>
              <span class="stat-label">生成记录</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ stats.voiceCount }}</span>
              <span class="stat-label">我的音色</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="16">
        <div class="glass-card setting-card">
          <el-tabs v-model="activeTab" class="anime-tabs">
            <el-tab-pane label="基本设置" name="base">
              <el-form label-position="top" class="mt-20">
                <el-form-item label="用户名">
                  <el-input :value="userStore.userInfo?.username" disabled />
                </el-form-item>
                <el-form-item label="注册邮箱">
                  <el-input :value="userStore.userInfo?.email" disabled />
                </el-form-item>
                <el-button type="primary" class="anime-btn" disabled>保存修改 (功能暂不可用)</el-button>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passForm" :rules="passRules" ref="passRef" label-position="top" class="mt-20">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="passForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-button type="primary" class="anime-btn" @click="handleUpdatePass" :loading="loading">
                  确认修改
                </el-button>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const userStore = useUserStore()
const activeTab = ref('base')
const loading = ref(false)
const passRef = ref()
const defaultAvatar = 'https://api.dicebear.com/7.x/adventurer/svg?seed=Lucky'

const stats = reactive({
  recordCount: 0,
  voiceCount: 0
})

const passForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '不少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (_: any, value: any, callback: any) => {
        if (value !== passForm.newPassword) callback(new Error('两次输入不一致'))
        else callback()
      }, trigger: 'blur' 
    }
  ]
}

const fetchStats = async () => {
  try {
    const recRes: any = await request.get('/tts/history', { params: { page: 1, size: 1 } })
    stats.recordCount = recRes.data.total
    
    const voiceRes: any = await request.get('/voice/list', { params: { page: 1, size: 1 } })
    stats.voiceCount = voiceRes.data.total
  } catch (err) {
    console.error('获取统计失败')
  }
}

const handleUpdatePass = async () => {
  if (!passRef.value) return
  await passRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/user/password/update', null, {
          params: {
            oldPassword: passForm.oldPassword,
            newPassword: passForm.newPassword
          }
        })
        ElMessage.success('密码修改成功，请重新登录')
        userStore.logout()
        window.location.reload()
      } catch (err: any) {
        ElMessage.error(err.message || '修改失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(fetchStats)
</script>

<style scoped>
.profile-container {
  padding-top: 10px;
}

.user-card {
  padding: 40px 20px;
  text-align: center;
}

.avatar-wrapper {
  margin-bottom: 20px;
  display: inline-block;
  padding: 5px;
  border: 4px solid var(--primary-color);
  border-radius: 50%;
}

.username {
  font-size: 22px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.email {
  font-size: 14px;
  color: #94a3b8;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 24px;
  font-weight: 800;
  color: var(--accent-color);
}

.stat-label {
  font-size: 12px;
  color: #64748b;
}

.setting-card {
  padding: 30px;
  min-height: 480px;
}

.mt-20 {
  margin-top: 20px;
}

:deep(.el-tabs__item.is-active) {
  color: var(--primary-color);
  font-weight: 700;
}

:deep(.el-tabs__active-bar) {
  background-color: var(--primary-color);
}
</style>

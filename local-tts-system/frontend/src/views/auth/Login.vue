<template>
  <div class="auth-container">
    <div class="auth-card glass-card floating">
      <div class="auth-header">
        <h2 class="text-gradient">欢迎回来</h2>
        <p>每一声问候，都是跨越次元的相遇</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="formRef" class="auth-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名" 
            :prefix-icon="UserIcon"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="LockIcon"
            show-password
            size="large"
          />
        </el-form-item>
        
        <div class="auth-options">
          <el-link type="primary" :underline="false" @click="router.push('/forget-password')">忘记密码？</el-link>
        </div>
        
        <el-button 
          type="primary" 
          class="anime-btn btn-full" 
          @click="handleLogin" 
          :loading="loading"
        >
          登 录
        </el-button>
        
        <div class="auth-footer">
          还没有账号？<el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { User as UserIcon, Lock as LockIcon } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res: any = await request.post('/auth/login', loginForm)
        userStore.setToken(res.data.token)
        
        // 获取用户信息
        const infoRes: any = await request.get('/user/info')
        userStore.setUserInfo(infoRes.data)
        
        ElMessage.success('登录成功，欢迎回来！')
        router.push('/')
      } catch (error: any) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.auth-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('https://api.kdcc.cn/img/rand/acg/'); /* 随机二次元背景 */
  background-size: cover;
  background-position: center;
}

.auth-card {
  width: 400px;
  padding: 40px;
  text-align: center;
}

.auth-header {
  margin-bottom: 30px;
}

.auth-header h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.auth-header p {
  color: #94a3b8;
  font-size: 14px;
}

.auth-form {
  margin-top: 20px;
}

.auth-options {
  text-align: right;
  margin-bottom: 20px;
}

.btn-full {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.auth-footer {
  margin-top: 20px;
  font-size: 14px;
  color: #64748b;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  box-shadow: none !important;
  border: 1px solid var(--glass-border);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
}
</style>

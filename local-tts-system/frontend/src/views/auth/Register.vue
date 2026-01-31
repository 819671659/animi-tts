<template>
  <div class="auth-container">
    <div class="auth-card glass-card floating">
      <div class="auth-header">
        <h2 class="text-gradient">新会员注册</h2>
        <p>开启属于你的声之物语</p>
      </div>
      
      <el-form :model="registerForm" :rules="rules" ref="formRef" class="auth-form">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="用户名" 
            :prefix-icon="UserIcon"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input 
            v-model="registerForm.email" 
            placeholder="电子邮箱" 
            :prefix-icon="MailIcon"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="code">
          <div class="code-input-group">
            <el-input 
              v-model="registerForm.code" 
              placeholder="验证码" 
              :prefix-icon="KeyIcon"
              size="large"
            />
            <el-button 
              class="anime-btn send-btn" 
              :disabled="countdown > 0" 
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="设置密码" 
            :prefix-icon="LockIcon"
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-button 
          type="primary" 
          class="anime-btn btn-full" 
          @click="handleRegister" 
          :loading="loading"
        >
          注 册
        </el-button>
        
        <div class="auth-footer">
          已有账号？<el-link type="primary" :underline="false" @click="router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User as UserIcon, Lock as LockIcon, Message as MailIcon, Key as KeyIcon } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const countdown = ref(0)

const registerForm = reactive({
  username: '',
  email: '',
  code: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ]
}

const handleSendCode = async () => {
  if (!registerForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  
  try {
    await request.post('/auth/code/send', { 
      email: registerForm.email,
      type: 'REGISTER'
    })
    ElMessage.success('验证码已发送，请查收邮件')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (error: any) {
    ElMessage.error(error.message || '发送失败')
  }
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/auth/register', registerForm)
        ElMessage.success('注册成功，请登录！')
        router.push('/login')
      } catch (error: any) {
        ElMessage.error(error.message || '注册失败')
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
  background-image: url('https://api.kdcc.cn/img/rand/acg/');
  background-size: cover;
  background-position: center;
}

.auth-card {
  width: 440px;
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

.code-input-group {
  display: flex;
  gap: 12px;
  width: 100%;
}

.send-btn {
  white-space: nowrap;
  padding: 0 15px;
  font-size: 13px;
}

.btn-full {
  width: 100%;
  height: 45px;
  font-size: 16px;
  margin-top: 10px;
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

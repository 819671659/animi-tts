import axios from 'axios'
import type { Result } from '@/types/tts'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
    baseURL: '/api',
    timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response: any) => {
        const res = response.data as Result<any>

        if (res.code === 200) {
            return res
        } else {
            return Promise.reject(new Error(res.message || '请求失败'))
        }
    },
    (error) => {
        if (error.response && error.response.status === 403) {
            const userStore = useUserStore()
            userStore.logout()
            router.push('/login')
        }
        return Promise.reject(error)
    }
)

export default request

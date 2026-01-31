import { defineStore } from 'pinia'
import request from '@/utils/request'

interface UserInfo {
    id: number
    username: string
    email: string
    avatar?: string
}

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: null as UserInfo | null,
        token: localStorage.getItem('token') || '',
    }),
    getters: {
        isLoggedIn: (state) => !!state.token,
    },
    actions: {
        setToken(token: string) {
            this.token = token
            localStorage.setItem('token', token)
        },
        setUserInfo(info: UserInfo) {
            this.userInfo = info
        },
        async fetchUserInfo() {
            try {
                const res: any = await request.get('/user/info')
                this.userInfo = res.data
                localStorage.setItem('userInfo', JSON.stringify(res.data))
            } catch (error) {
                console.error('获取用户信息失败', error)
            }
        },
        logout() {
            this.token = ''
            this.userInfo = null
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
        },
    },
})

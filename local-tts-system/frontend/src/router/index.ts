import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/auth/Login.vue'),
        meta: { public: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/auth/Register.vue'),
        meta: { public: true }
    },
    {
        path: '/forget-password',
        name: 'ForgetPassword',
        component: () => import('../views/auth/ForgetPassword.vue'),
        meta: { public: true }
    },
    {
        path: '/',
        component: () => import('../layout/MainLayout.vue'),
        children: [
            {
                path: '',
                name: 'Home',
                component: () => import('../views/tts/Generate.vue')
            },
            {
                path: 'history',
                name: 'History',
                component: () => import('../views/history/HistoryList.vue')
            },
            {
                path: 'voices',
                name: 'Voices',
                component: () => import('../views/voice/VoiceManager.vue')
            },
            {
                path: 'square',
                name: 'Square',
                component: () => import('../views/voice/VoiceSquare.vue')
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/user/Profile.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    if (!to.meta.public && !userStore.isLoggedIn) {
        next('/login')
    } else if (to.meta.public && userStore.isLoggedIn && to.path === '/login') {
        next('/')
    } else {
        next()
    }
})

export default router

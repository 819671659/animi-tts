<template>
  <div class="voice-square-container">
    <div class="square-header glass-card">
      <div class="header-content">
        <h2 class="text-gradient">次元音色广场</h2>
        <p>发现来自不同次元的心动瞬间，让灵感在声音中碰撞</p>
      </div>
      <el-button link @click="fetchPublicVoices"><el-icon><Refresh /></el-icon> 刷新广场</el-button>
    </div>

    <div v-loading="loading" class="square-grid">
      <div v-for="voice in publicVoices" :key="voice.id" class="glass-card voice-card">
        <div class="voice-badge">共享中</div>
        <div class="avatar-area">
          <el-avatar :size="64" :src="voice.avatar || defaultAvatar" class="floating" />
        </div>
        
        <div class="voice-info">
          <h4 class="voice-name">{{ voice.voiceName }}</h4>
          <p class="voice-type">所属次元: AI 训练生</p>
          <p class="voice-desc">{{ voice.description || '这位声优很神秘，什么都没留下...' }}</p>
        </div>

        <div class="voice-actions">
          <el-button type="primary" link @click="playSample(voice)">
            <el-icon><VideoPlay /></el-icon> 试听
          </el-button>
          <el-button type="primary" class="anime-btn use-btn" @click="useThisVoice(voice)">
            立即使用
          </el-button>
        </div>
      </div>

      <div v-if="!publicVoices.length && !loading" class="empty-state">
        <el-empty description="广场上空荡荡的，去第一个分享自己的音色吧！" />
      </div>
    </div>

    <audio ref="playerRef"></audio>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Refresh, VideoPlay } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useTtsStore } from '@/stores/tts'
import type { CustomVoice } from '@/types/tts'

const router = useRouter()
const ttsStore = useTtsStore()
const publicVoices = ref<CustomVoice[]>([])
const loading = ref(false)
const playerRef = ref<HTMLAudioElement>()
const defaultAvatar = 'https://api.dicebear.com/7.x/adventurer/svg?seed=Lucky'

const fetchPublicVoices = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/voice/square')
    publicVoices.value = res.data
  } catch (err) {
    ElMessage.error('获取广场数据失败')
  } finally {
    loading.value = false
  }
}

const playSample = (voice: any) => {
  if (!playerRef.value) return
  playerRef.value.src = `/api/tts/download/${voice.sampleAudioUrl}`
  playerRef.value.play()
}

const useThisVoice = (voice: any) => {
  ttsStore.setVoice('custom_' + voice.id)
  ElMessage.success(`已选择音色: ${voice.voiceName}`)
  router.push('/')
}

onMounted(fetchPublicVoices)
</script>

<style scoped>
.voice-square-container {
  padding-top: 10px;
}

.square-header {
  padding: 30px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.header-content p {
  color: #64748b;
  font-size: 14px;
}

.square-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.voice-card {
  padding: 24px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.voice-badge {
  position: absolute;
  top: 10px;
  right: -30px;
  background: var(--primary-color);
  color: white;
  padding: 2px 30px;
  transform: rotate(45deg);
  font-size: 10px;
  font-weight: 700;
}

.avatar-area {
  margin-bottom: 16px;
}

.voice-name {
  font-size: 18px;
  margin-bottom: 4px;
  color: var(--text-primary);
}

.voice-type {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 12px;
}

.voice-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  margin-bottom: 20px;
}

.voice-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.use-btn {
  padding: 8px 16px;
  font-size: 13px;
}

.empty-state {
  grid-column: 1 / -1;
  padding: 80px 0;
}
</style>

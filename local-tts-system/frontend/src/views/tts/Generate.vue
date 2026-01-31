<template>
  <div class="generate-container">
    <div class="glass-card editor-card">
      <div class="editor-header">
        <h3>文本编辑</h3>
        <span class="word-count">{{ ttsStore.inputText.length }} / 500</span>
      </div>
      <el-input
        v-model="ttsStore.inputText"
        type="textarea"
        :rows="10"
        placeholder="在这里输入你想要变成声音的文字..."
        resize="none"
        class="anime-textarea"
      />
      <div class="editor-footer">
        <el-button type="info" link @click="ttsStore.setText('')">清空文本</el-button>
      </div>
    </div>

    <div class="glass-card settings-card">
      <h3>语音设置</h3>
      
      <div class="setting-item">
        <label>选择声音</label>
        <el-select v-model="ttsStore.selectedVoice" placeholder="请选择声优" class="anime-select" size="large">
          <el-option-group label="系统预设">
            <el-option
              v-for="item in ttsStore.voiceList"
              :key="'preset_' + item.id"
              :label="item.configName"
              :value="'preset_' + item.id"
            />
          </el-option-group>
          <el-option-group label="我的音色" v-if="ttsStore.customVoiceList.length">
            <el-option
              v-for="item in ttsStore.customVoiceList"
              :key="item.id"
              :label="item.voiceName"
              :value="'custom_' + item.id"
            />
          </el-option-group>
        </el-select>
      </div>

      <div class="setting-item">
        <div class="slider-header">
          <span>语速</span>
          <span>{{ ttsStore.getRateString() }}</span>
        </div>
        <el-slider v-model="ttsStore.rate" :min="-50" :max="100" class="anime-slider" />
      </div>

      <div class="setting-item">
        <div class="slider-header">
          <span>音调</span>
          <span>{{ ttsStore.getPitchString() }}</span>
        </div>
        <el-slider v-model="ttsStore.pitch" :min="-50" :max="50" class="anime-slider" />
      </div>

      <el-button 
        type="primary" 
        class="anime-btn generate-btn" 
        :loading="loading" 
        @click="handleGenerate"
      >
        <el-icon class="el-icon--left"><Mic /></el-icon>
        赋予灵魂 (生成语音)
      </el-button>
    </div>

    <!-- 播放器区域 -->
    <transition name="el-zoom-in-bottom">
      <div v-if="ttsStore.currentAudioUrl" class="glass-card player-card floating">
        <div class="player-info">
          <span class="player-title">生成成功！</span>
          <audio ref="audioRef" :src="ttsStore.currentAudioUrl" controls class="anime-audio"></audio>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTtsStore } from '@/stores/tts'
import { Mic } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const ttsStore = useTtsStore()
const loading = ref(false)
const audioRef = ref<HTMLAudioElement>()

onMounted(async () => {
  // 加载语音列表
  try {
    const res: any = await request.get('/tts/voices')
    ttsStore.voiceList = res.data
    
    // 加载个人音色
    const customRes: any = await request.get('/voice/list', { params: { page: 1, size: 50 } })
    ttsStore.customVoiceList = customRes.data.records
    
    if (ttsStore.voiceList.length && !ttsStore.selectedVoice) {
      ttsStore.selectedVoice = 'preset_' + ttsStore.voiceList[0].id
    }
  } catch (err) {
    ElMessage.error('加载列表失败')
  }
})

const handleGenerate = async () => {
  if (!ttsStore.inputText.trim()) {
    ElMessage.warning('请输入文本')
    return
  }
  
  loading.value = true
  try {
    const isCustom = ttsStore.selectedVoice.startsWith('custom_')
    const res: any = await request.post('/tts/generate', {
      text: ttsStore.inputText,
      voiceName: isCustom ? ttsStore.selectedVoice.replace('custom_', '') : ttsStore.selectedVoice,
      rate: ttsStore.getRateString(),
      pitch: ttsStore.getPitchString(),
      useCustomVoice: isCustom
    })
    
    ttsStore.setAudioUrl(res.data.audioUrl)
    ElMessage.success('赋予灵魂成功！')
  } catch (error: any) {
    ElMessage.error(error.message || '生成失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.generate-container {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 20px;
  padding-top: 10px;
}

.editor-card {
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.word-count {
  font-size: 12px;
  color: #94a3b8;
}

.anime-textarea :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 15px;
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-color);
  box-shadow: none !important;
}

.editor-footer {
  margin-top: 10px;
  text-align: right;
}

.settings-card {
  padding: 24px;
}

.settings-card h3 {
  margin-bottom: 20px;
  color: var(--text-primary);
}

.setting-item {
  margin-bottom: 24px;
}

.setting-item label {
  display: block;
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: 600;
}

.slider-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 5px;
}

.anime-select {
  width: 100%;
}

.generate-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  margin-top: 10px;
}

.player-card {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  width: 500px;
  padding: 20px;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.player-title {
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 10px;
  display: block;
  text-align: center;
}

.anime-audio {
  width: 100%;
  height: 40px;
}

/* Element Plus 样式覆盖 */
:deep(.el-slider__bar) {
  background: linear-gradient(to right, var(--primary-color), var(--accent-color));
}

:deep(.el-slider__button) {
  border-color: var(--primary-color);
}
</style>

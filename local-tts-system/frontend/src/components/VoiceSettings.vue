<template>
  <div class="voice-settings">
    <h3>语音设置</h3>
    
    <div class="setting-item">
      <label>语音角色</label>
      <select v-model="selectedVoice" class="voice-select">
        <option value="">请选择语音...</option>
        <option 
          v-for="voice in voiceList" 
          :key="voice.id" 
          :value="voice.voiceName"
        >
          {{ voice.configName }} - {{ voice.description }}
        </option>
      </select>
    </div>
    
    <div class="setting-item">
      <label>语速: {{ rateDisplay }}</label>
      <input
        v-model.number="rate"
        type="range"
        min="-50"
        max="100"
        step="10"
        class="slider"
      />
      <div class="slider-labels">
        <span>慢</span>
        <span>标准</span>
        <span>快</span>
      </div>
    </div>
    
    <div class="setting-item">
      <label>音调: {{ pitchDisplay }}</label>
      <input
        v-model.number="pitch"
        type="range"
        min="-50"
        max="50"
        step="10"
        class="slider"
      />
      <div class="slider-labels">
        <span>低</span>
        <span>标准</span>
        <span>高</span>
      </div>
    </div>
    
    <div class="actions">
      <button @click="resetSettings" class="btn-secondary">重置</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useTtsStore } from '@/stores/tts'
import { getVoiceList } from '@/api/tts'

const ttsStore = useTtsStore()

const selectedVoice = computed({
  get: () => ttsStore.selectedVoice,
  set: (value) => {
    ttsStore.setVoice(value)
    // 自动应用该语音的预设语速和音调
    const voice = ttsStore.voiceList.find(v => v.voiceName === value)
    if (voice) {
      // 解析 "+10%" 为 10，"-5%" 为 -5
      const rateVal = parseInt(voice.rate.replace('%', '')) || 0
      // 解析 "+15Hz" 为 15，"-10Hz" 为 -10
      const pitchVal = parseInt(voice.pitch.replace('Hz', '')) || 0
      
      ttsStore.setRate(rateVal)
      ttsStore.setPitch(pitchVal)
    }
  }
})

const rate = computed({
  get: () => ttsStore.rate,
  set: (value) => ttsStore.setRate(value)
})

const pitch = computed({
  get: () => ttsStore.pitch,
  set: (value) => ttsStore.setPitch(value)
})

const voiceList = computed(() => ttsStore.voiceList)

const rateDisplay = computed(() => `${rate.value >= 0 ? '+' : ''}${rate.value}%`)
const pitchDisplay = computed(() => `${pitch.value >= 0 ? '+' : ''}${pitch.value}Hz`)

function resetSettings() {
  ttsStore.setRate(0)
  ttsStore.setPitch(0)
}

// 加载语音列表
onMounted(async () => {
  console.log('开始加载语音列表...')
  try {
    const res = await getVoiceList()
    console.log('API 响应完整数据:', res)
    console.log('响应数据类型:', typeof res.data)
    console.log('res.data:', res.data)
    
    // 注意：Axios 拦截器已经返回了 { code, message, data } 格式
    // 所以这里 res 就是 { code: 200, message: "...", data: [...] }
    // 直接访问 res.data 即可
    if (res.data) {
      ttsStore.voiceList = res.data
      console.log('成功设置语音列表，数量:', ttsStore.voiceList.length)
      console.log('语音列表内容:', ttsStore.voiceList)
      
      // 默认选中第一个语音
      if (ttsStore.voiceList.length > 0 && !ttsStore.selectedVoice) {
        ttsStore.setVoice(ttsStore.voiceList[0].voiceName)
        console.log('默认选中语音:', ttsStore.voiceList[0].voiceName)
      }
    } else {
      console.error('数据格式异常:', res)
    }
  } catch (error) {
    console.error('加载语音列表失败:', error)
  }
})
</script>

<style scoped>
.voice-settings {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.voice-settings h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.setting-item {
  margin-bottom: 24px;
}

.setting-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #555;
}

.voice-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.3s;
}

.voice-select:focus {
  outline: none;
  border-color: #4a90e2;
}

.slider {
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background: #e0e0e0;
  outline: none;
  cursor: pointer;
}

.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #4a90e2;
  cursor: pointer;
  transition: background 0.3s;
}

.slider::-webkit-slider-thumb:hover {
  background: #3a7bc8;
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.btn-secondary {
  padding: 8px 16px;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: #e0e0e0;
}
</style>

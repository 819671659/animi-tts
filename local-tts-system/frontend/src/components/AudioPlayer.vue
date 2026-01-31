<template>
  <div class="audio-player">
    <h3>音频播放器</h3>
    
    <div v-if="!audioUrl" class="no-audio">
      <p>暂无音频</p>
    </div>
    
    <div v-else class="player-container">
      <audio
        ref="audioRef"
        :src="audioUrl"
        controls
        class="audio-element"
      ></audio>
      
      <div class="player-actions">
        <a :href="audioUrl" :download="audioFilename" class="btn-download">
          📥 下载音频
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useTtsStore } from '@/stores/tts'

const ttsStore = useTtsStore()
const audioRef = ref<HTMLAudioElement | null>(null)

const audioUrl = computed(() => ttsStore.currentAudioUrl)

const audioFilename = computed(() => {
  if (!audioUrl.value) return 'audio.mp3'
  const parts = audioUrl.value.split('/')
  return parts[parts.length - 1]
})
</script>

<style scoped>
.audio-player {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.audio-player h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.no-audio {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.no-audio p {
  margin: 0;
  font-size: 14px;
}

.player-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audio-element {
  width: 100%;
  outline: none;
}

.player-actions {
  display: flex;
  justify-content: center;
}

.btn-download {
  padding: 10px 20px;
  background: #4a90e2;
  color: #fff;
  text-decoration: none;
  border-radius: 6px;
  font-size: 14px;
  transition: background 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-download:hover {
  background: #3a7bc8;
}
</style>

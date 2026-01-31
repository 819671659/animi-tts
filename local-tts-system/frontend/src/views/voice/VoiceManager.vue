<template>
  <div class="voice-manager-container">
    <div class="header-actions">
      <el-button type="primary" class="anime-btn" @click="showUploadDialog = true">
        <el-icon class="el-icon--left"><Upload /></el-icon> 上传音色样本
      </el-button>
      <el-button type="success" class="anime-btn btn-record" @click="showRecordDialog = true">
        <el-icon class="el-icon--left"><Mic /></el-icon> 在线录制
      </el-button>
    </div>

    <div class="voice-grid">
      <div v-for="voice in myVoices" :key="voice.id" class="glass-card voice-card">
        <div class="voice-card-header">
          <span class="voice-name text-gradient">{{ voice.voiceName }}</span>
          <el-tag :type="voice.isPublic ? 'success' : 'info'" round size="small" class="share-tag">
            {{ voice.isPublic ? '已分享' : '私有' }}
          </el-tag>
        </div>
        
        <p class="voice-desc">{{ voice.description || '暂无描述' }}</p>
        
        <div class="voice-stats">
          <span>状态: {{ getStatusText(voice.trainingStatus) }}</span>
        </div>

        <div class="voice-actions">
          <el-switch
            v-model="voice.isPublic"
            :active-value="1"
            :inactive-value="0"
            @change="(val: number) => handleShareToggle(voice, val)"
            inline-prompt
            active-text="公开"
            inactive-text="隐藏"
            class="anime-switch"
          />
          <div class="btn-group">
            <el-button circle size="small" type="primary" @click="playSample(voice)">
              <el-icon><VideoPlay /></el-icon>
            </el-button>
            <el-button circle size="small" type="danger" @click="confirmDelete(voice)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空白占位 -->
      <div v-if="!myVoices.length" class="empty-state">
        <el-empty description="还没有自己的音色哦，快来上传或录制一个吧~" />
      </div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="showUploadDialog" title="上传音色样本" width="450px" class="anime-dialog">
      <el-form :model="uploadForm" label-position="top">
        <el-form-item label="音色名称">
          <el-input v-model="uploadForm.voiceName" placeholder="给你的次元分身起个名字吧" />
        </el-form-item>
        <el-form-item label="描述 (可选)">
          <el-input v-model="uploadForm.description" type="textarea" placeholder="描述一下这个声音的特点..." />
        </el-form-item>
        <el-form-item label="音频文件 (建议 15-30s)">
          <el-upload
            class="anime-upload"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            accept=".wav,.mp3"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip anime-tip">
                支持 WAV/MP3 格式，建议长度 15-30 秒以获得最佳效果
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showUploadDialog = false">取消</el-button>
          <el-button type="primary" class="anime-btn" @click="submitUpload" :loading="uploading">开始上传</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 录音对话框 -->
    <el-dialog v-model="showRecordDialog" title="在线录制样本" width="450px" class="anime-dialog" @close="stopRecording">
      <div class="recorder-wrapper">
        <p class="record-tip">请阅读以下文本以便模型学习：</p>
        <div class="sample-text glass-card">
          “你好，我是你的次元分身。很高兴能以这种方式与你相遇，希望我的声音能带给你快乐。”
        </div>
        
        <div class="record-controls">
          <div class="timer" v-if="recording">{{ formatTime(recordTime) }}</div>
          <el-button 
            type="danger" 
            circle 
            class="record-btn" 
            :class="{ 'is-recording': recording }"
            @click="toggleRecording"
          >
            <el-icon v-if="!recording" size="30"><Mic /></el-icon>
            <div v-else class="stop-icon"></div>
          </el-button>
        </div>

        <div v-if="recordedBlob" class="preview-area">
          <audio :src="previewUrl" controls class="preview-audio"></audio>
          <el-input v-model="recordVoiceName" placeholder="输入音色名称" class="mt-20" />
        </div>
      </div>
      <template #footer>
        <el-button @click="showRecordDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          class="anime-btn" 
          :disabled="!recordedBlob || !recordVoiceName" 
          @click="uploadRecording"
          :loading="uploading"
        >确认使用</el-button>
      </template>
    </el-dialog>

    <audio ref="playerRef"></audio>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Upload, Mic, VideoPlay, Delete, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import type { CustomVoice } from '@/types/tts'

const myVoices = ref<CustomVoice[]>([])
const showUploadDialog = ref(false)
const showRecordDialog = ref(false)
const uploading = ref(false)

const uploadForm = reactive({
  voiceName: '',
  description: '',
  file: null as File | null
})

const fetchMyVoices = async () => {
  try {
    const res: any = await request.get('/voice/list', { params: { page: 1, size: 50 } })
    myVoices.value = res.data.records
  } catch (err) {
    ElMessage.error('获取音色列表失败')
  }
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '待训练', 1: '训练中', 2: '可用', 3: '失败' }
  return map[status] || '未知'
}

const handleFileChange = (file: any) => {
  uploadForm.file = file.raw
}

const submitUpload = async () => {
  if (!uploadForm.file || !uploadForm.voiceName) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  uploading.value = true
  const formData = new FormData()
  formData.append('audioFile', uploadForm.file)
  formData.append('voiceName', uploadForm.voiceName)
  formData.append('description', uploadForm.description)
  formData.append('sampleText', '用户上传样本')

  try {
    await request.post('/voice/upload', formData)
    ElMessage.success('上传成功，请等待后台处理（训练演示）')
    showUploadDialog.value = false
    fetchMyVoices()
  } catch (err: any) {
    ElMessage.error(err.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

const handleShareToggle = async (voice: any, val: number) => {
  try {
    await request.post(`/voice/share/${voice.id}`, null, { params: { isPublic: val } })
    ElMessage.success(val ? '已开启公开分享' : '已设为私有')
  } catch (err) {
    ElMessage.error('操作失败')
    voice.isPublic = val ? 0 : 1 // 还原
  }
}

const confirmDelete = (voice: any) => {
  ElMessageBox.confirm('确定要彻底删除这个音色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/voice/delete/${voice.id}`)
      ElMessage.success('已移除次元分身')
      fetchMyVoices()
    } catch (err) {
      ElMessage.error('删除失败')
    }
  })
}

// 录音逻辑
const recording = ref(false)
const recordTime = ref(0)
const recordedBlob = ref<Blob | null>(null)
const previewUrl = ref('')
const recordVoiceName = ref('')
let mediaRecorder: MediaRecorder | null = null
let timer: any = null

const toggleRecording = async () => {
  if (recording.value) {
    stopRecording()
  } else {
    startRecording()
  }
}

const startRecording = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder = new MediaRecorder(stream)
    const chunks: Blob[] = []
    
    mediaRecorder.ondataavailable = (e) => chunks.push(e.data)
    mediaRecorder.onstop = () => {
      recordedBlob.value = new Blob(chunks, { type: 'audio/wav' })
      previewUrl.value = URL.createObjectURL(recordedBlob.value)
    }
    
    mediaRecorder.start()
    recording.value = true
    recordTime.value = 0
    timer = setInterval(() => recordTime.value++, 1000)
  } catch (err) {
    ElMessage.error('无法启用麦克风')
  }
}

const stopRecording = () => {
  if (mediaRecorder && recording.value) {
    mediaRecorder.stop()
    mediaRecorder.stream.getTracks().forEach(track => track.stop())
    recording.value = false
    clearInterval(timer)
  }
}

const formatTime = (s: number) => {
  const m = Math.floor(s / 60)
  const ss = s % 60
  return `${m}:${ss < 10 ? '0' : ''}${ss}`
}

const uploadRecording = async () => {
  if (!recordedBlob.value || !recordVoiceName.value) return
  
  uploading.value = true
  const formData = new FormData()
  formData.append('audioFile', recordedBlob.value, 'record.wav')
  formData.append('voiceName', recordVoiceName.value)
  formData.append('description', '在线录制样本')
  formData.append('sampleText', '在线录制文案')

  try {
    await request.post('/voice/upload', formData)
    ElMessage.success('录制样本已上传')
    showRecordDialog.value = false
    fetchMyVoices()
  } catch (err: any) {
    ElMessage.error(err.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

const playerRef = ref<HTMLAudioElement>()
const playSample = (voice: any) => {
  if (!playerRef.value) return
  playerRef.value.src = voice.sampleAudioUrl
  playerRef.value.play()
}

onMounted(fetchMyVoices)
</script>

<style scoped>
.voice-manager-container {
  padding-top: 10px;
}

.header-actions {
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
}

.btn-record {
  background: linear-gradient(to right, #10b981, #3b82f6);
}

.voice-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.voice-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.voice-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.voice-name {
  font-size: 18px;
  font-weight: 700;
}

.voice-desc {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 20px;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.voice-stats {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 15px;
}

.voice-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.btn-group {
  display: flex;
  gap: 8px;
}

.empty-state {
  grid-column: 1 / -1;
  padding: 100px 0;
}

/* 录音器样式 */
.recorder-wrapper {
  text-align: center;
}

.record-tip {
  margin-bottom: 15px;
  color: #64748b;
}

.sample-text {
  padding: 20px;
  margin-bottom: 30px;
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
}

.record-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.timer {
  font-size: 24px;
  font-weight: 800;
  color: #ef4444;
}

.record-btn {
  width: 80px;
  height: 80px;
  border: 4px solid #fecaca;
  transition: all 0.3s;
}

.record-btn.is-recording {
  animation: pulse 1.5s infinite;
  border-color: #fca5a5;
}

.stop-icon {
  width: 24px;
  height: 24px;
  background: white;
  border-radius: 4px;
}

@keyframes pulse {
  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4); }
  70% { transform: scale(1.1); box-shadow: 0 0 0 20px rgba(239, 68, 68, 0); }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); }
}

.preview-area {
  margin-top: 30px;
  padding: 20px;
  border-top: 1px solid var(--glass-border);
}

.preview-audio {
  width: 100%;
  height: 35px;
}

.mt-20 {
  margin-top: 20px;
}

.mt-20 :deep(.el-input__wrapper) {
  border-radius: 20px;
}

/* 拖拽上传样式 */
.anime-upload :deep(.el-upload-dragger) {
  background: rgba(255, 255, 255, 0.4);
  border: 2px dashed var(--glass-border);
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.anime-upload :deep(.el-upload-dragger:hover) {
  border-color: var(--primary-color);
  background: rgba(255, 133, 161, 0.05);
  transform: translateY(-2px);
}

.anime-upload :deep(.el-icon--upload) {
  color: var(--primary-color);
  font-size: 54px;
  margin-bottom: 10px;
}

.el-upload__text em {
  color: var(--primary-color);
  font-weight: 600;
  font-style: normal;
}

.anime-tip {
  color: #94a3b8;
  font-size: 12px;
  margin-top: 8px;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

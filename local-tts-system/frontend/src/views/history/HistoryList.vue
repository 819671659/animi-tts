<template>
  <div class="history-container">
    <div class="glass-card table-card">
      <div class="table-header">
        <div class="header-left">
          <h3>生成历史</h3>
          <span class="subtitle">记录每一份注入灵魂的瞬间</span>
        </div>
        <el-button link @click="fetchHistory"><el-icon><Refresh /></el-icon> 刷新</el-button>
      </div>

      <el-table :data="historyList" v-loading="loading" style="width: 100%" class="anime-table">
        <el-table-column label="音频内容" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="text-content" @click="handleRename(row)">
              {{ row.textContent }}
              <el-icon class="edit-icon"><EditPen /></el-icon>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="音色" width="180">
          <template #default="{ row }">
            <el-tag effect="plain" round class="voice-tag">{{ row.voiceName }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="文件大小" width="100">
          <template #default="{ row }">
            {{ (row.fileSize / 1024).toFixed(1) }} KB
          </template>
        </el-table-column>
        
        <el-table-column label="生成时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button 
                circle 
                :type="currentPlaying === row.id ? 'success' : 'primary'" 
                @click="playAudio(row)"
              >
                <el-icon><VideoPlay v-if="currentPlaying !== row.id"/><Service v-else/></el-icon>
              </el-button>
              
              <el-button circle type="info" @click="downloadFile(row)">
                <el-icon><Download /></el-icon>
              </el-button>
              
              <el-button circle type="danger" @click="confirmDelete(row)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchHistory"
          class="anime-pagination"
        />
      </div>
    </div>

    <!-- 隐藏的音频播放器 -->
    <audio ref="playerRef" @ended="currentPlaying = null" @error="handlePlayError"></audio>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Refresh, VideoPlay, Download, Delete, Service, EditPen } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const historyList = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const currentPlaying = ref<number | null>(null)
const playerRef = ref<HTMLAudioElement>()

const fetchHistory = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/tts/history', {
      params: { page: page.value, size: size.value }
    })
    historyList.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    ElMessage.error('获取历史失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr: string) => {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const playAudio = (row: any) => {
  if (!playerRef.value) return
  
  if (currentPlaying.value === row.id) {
    playerRef.value.pause()
    currentPlaying.value = null
    return
  }
  
  // 使用新的静态资源映射路径
  const url = `/api/audios/${row.audioUrl}`
  playerRef.value.src = url
  playerRef.value.play()
  currentPlaying.value = row.id
}

const handlePlayError = () => {
  ElMessage.error('音频播放失败，文件可能已被删除')
  currentPlaying.value = null
}

const downloadFile = (row: any) => {
  // 使用新的静态资源映射路径
  const url = `/api/audios/${row.audioUrl}`
  const link = document.createElement('a')
  link.href = url
  link.download = `${row.textContent.substring(0, 10)}.mp3`
  link.click()
}

const handleRename = (row: any) => {
  ElMessageBox.prompt('请输入新的记录名称（备注）', '重命名', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: row.textContent,
  }).then(async ({ value }) => {
    if (!value) return
    try {
      await request.post(`/tts/rename/${row.id}`, null, { params: { newName: value } })
      ElMessage.success('重命名成功')
      fetchHistory()
    } catch (err) {
      ElMessage.error('重命名失败')
    }
  })
}

const confirmDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除此生成记录吗？源文件也将被物理删除。', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/tts/delete/${row.id}`)
      ElMessage.success('删除成功')
      fetchHistory()
    } catch (err) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(fetchHistory)
</script>

<style scoped>
.history-container {
  padding-top: 10px;
}

.table-card {
  padding: 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
}

.header-left h3 {
  color: var(--text-primary);
  margin-bottom: 4px;
}

.subtitle {
  font-size: 13px;
  color: #94a3b8;
}

.text-content {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.edit-icon {
  opacity: 0;
  transition: opacity 0.2s;
  color: var(--primary-color);
}

.text-content:hover .edit-icon {
  opacity: 1;
}

.voice-tag {
  color: var(--text-primary);
  border-color: rgba(236, 72, 153, 0.2);
}

.actions {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

:deep(.el-table) {
  background: transparent;
}

:deep(.el-table tr) {
  background: transparent;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: rgba(255, 133, 161, 0.05);
}
</style>

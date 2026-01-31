<template>
  <div class="text-editor">
    <div class="editor-header">
      <h3>文本编辑区</h3>
      <span class="char-count">字数: {{ charCount }}</span>
    </div>
    <textarea
      v-model="text"
      class="editor-textarea"
      placeholder="请输入要转换的文本..."
      @input="handleInput"
    ></textarea>
    <div class="editor-actions">
      <button @click="clearText" class="btn-secondary">清空</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useTtsStore } from '@/stores/tts'

const ttsStore = useTtsStore()
const text = computed({
  get: () => ttsStore.inputText,
  set: (value) => ttsStore.setText(value)
})

const charCount = computed(() => text.value.length)

function handleInput() {
  // 自动保存到 store
}

function clearText() {
  text.value = ''
}
</script>

<style scoped>
.text-editor {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.editor-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.char-count {
  font-size: 14px;
  color: #666;
}

.editor-textarea {
  width: 100%;
  min-height: 150px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.3s;
}

.editor-textarea:focus {
  outline: none;
  border-color: #4a90e2;
}

.editor-actions {
  margin-top: 12px;
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

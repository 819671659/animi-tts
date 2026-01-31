import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { VoiceConfig, TtsRecord, CustomVoice } from '@/types/tts'

/**
 * TTS 状态管理
 */
export const useTtsStore = defineStore('tts', () => {
    // 当前输入的文本
    const inputText = ref('')

    // 选中的语音
    const selectedVoice = ref('')

    // 语速
    const rate = ref(0) // -50 到 100

    // 音调
    const pitch = ref(0) // -50 到 50

    // 可用语音列表
    const voiceList = ref<VoiceConfig[]>([])

    // 历史记录
    const historyList = ref<TtsRecord[]>([])

    // 自定义音色列表
    const customVoiceList = ref<CustomVoice[]>([])

    // 当前播放的音频 URL
    const currentAudioUrl = ref('')

    // 加载状态
    const loading = ref(false)

    // 设置文本
    function setText(text: string) {
        inputText.value = text
    }

    // 设置语音
    function setVoice(voice: string) {
        selectedVoice.value = voice
    }

    // 设置语速
    function setRate(value: number) {
        rate.value = value
    }

    // 设置音调
    function setPitch(value: number) {
        pitch.value = value
    }

    // 设置音频 URL
    function setAudioUrl(url: string) {
        currentAudioUrl.value = url
    }

    // 格式化语速为字符串
    function getRateString() {
        return rate.value >= 0 ? `+${rate.value}%` : `${rate.value}%`
    }

    // 格式化音调为字符串
    function getPitchString() {
        return pitch.value >= 0 ? `+${pitch.value}Hz` : `${pitch.value}Hz`
    }

    return {
        inputText,
        selectedVoice,
        rate,
        pitch,
        voiceList,
        historyList,
        customVoiceList,
        currentAudioUrl,
        loading,
        setText,
        setVoice,
        setRate,
        setPitch,
        setAudioUrl,
        getRateString,
        getPitchString
    }
})

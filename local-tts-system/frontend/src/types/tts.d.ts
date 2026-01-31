/**
 * TTS 相关类型定义
 */

// TTS 生成请求
export interface TtsRequest {
    text: string
    voiceName: string
    rate?: string
    pitch?: string
    useCustomVoice?: boolean
}

// TTS 生成响应
export interface TtsResponse {
    audioUrl: string
    recordId: number
    fileSize: number
    duration: number
}

// 语音配置
export interface VoiceConfig {
    id: number
    configName: string
    voiceName: string
    rate: string
    pitch: string
    description: string
}

// TTS 记录
export interface TtsRecord {
    id: number
    textContent: string
    voiceName: string
    rate: string
    pitch: string
    audioUrl: string
    fileSize: number
    duration: number
    createTime: string
}

// 自定义音色
export interface CustomVoice {
    id: number
    voiceName: string
    sampleAudioUrl: string
    modelPath?: string
    trainingStatus: number // 0-待训练，1-训练中，2-已完成，3-失败
    isPublic: number // 0-私有，1-公开
    sampleDuration?: number
    sampleText?: string
    description?: string
    avatar?: string
    createTime: string
}

// 统一响应体
export interface Result<T> {
    code: number
    message: string
    data: T
}

// 分页响应
export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
    pages: number
}

import request from '@/utils/request'
import type {
    TtsRequest,
    TtsResponse,
    VoiceConfig,
    TtsRecord,
    CustomVoice,
    Result,
    PageResult
} from '@/types/tts'

/**
 * 生成语音
 */
export function generateTts(data: TtsRequest) {
    return request.post<Result<TtsResponse>>('/tts/generate', data)
}

/**
 * 获取可用语音列表
 */
export function getVoiceList() {
    return request.get<Result<VoiceConfig[]>>('/tts/voices')
}

/**
 * 下载音频文件
 */
export function downloadAudio(filename: string) {
    return `/api/tts/download/${filename}`
}

/**
 * 获取历史记录
 */
export function getHistory(page: number = 1, size: number = 10) {
    return request.get<Result<PageResult<TtsRecord>>>('/tts/history', {
        params: { page, size }
    })
}

/**
 * 删除历史记录
 */
export function deleteRecord(id: number) {
    return request.delete<Result<void>>(`/tts/delete/${id}`)
}

/**
 * 上传自定义音色样本
 */
export function uploadVoiceSample(formData: FormData) {
    return request.post<Result<CustomVoice>>('/voice/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 获取自定义音色列表
 */
export function getCustomVoices(page: number = 1, size: number = 10) {
    return request.get<Result<PageResult<CustomVoice>>>('/voice/list', {
        params: { page, size }
    })
}

/**
 * 训练音色模型
 */
export function trainVoice(id: number) {
    return request.post<Result<void>>(`/voice/train/${id}`)
}

/**
 * 删除自定义音色
 */
export function deleteCustomVoice(id: number) {
    return request.delete<Result<void>>(`/voice/delete/${id}`)
}

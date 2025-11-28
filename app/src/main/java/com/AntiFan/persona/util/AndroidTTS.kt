package com.AntiFan.persona.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * 简单的 Android 原生 TTS 封装
 */
class AndroidTTS(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // 设置语言 (默认跟随系统，通常支持中文)
                val result = tts?.setLanguage(Locale.CHINA) // 或者 Locale.getDefault()
                isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    /**
     * 朗读文本
     * @param text 要读的内容
     * @param personality 根据性格关键词调整音色 (模拟变声)
     */
    fun speak(text: String, personality: String = "") {
        if (!isReady || tts == null) return

        // --- 简单的音色模拟逻辑 ---
        if (personality.contains("猫") || personality.contains("可爱") || personality.contains("萝莉")) {
            tts?.setPitch(1.4f) // 音调变高 (尖细)
            tts?.setSpeechRate(1.2f) // 语速变快
        } else if (personality.contains("冷酷") || personality.contains("沉稳") || personality.contains("大佬")) {
            tts?.setPitch(0.7f) // 音调变低 (深沉)
            tts?.setSpeechRate(0.8f) // 语速变慢
        } else {
            tts?.setPitch(1.0f) // 正常
            tts?.setSpeechRate(1.0f)
        }

        // 开始朗读 (QUEUE_FLUSH 表示打断上一句，直接读新的)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    /**
     * 停止朗读
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * 销毁资源 (退出页面时调用)
     */
    fun shutdown() {
        tts?.shutdown()
    }
}
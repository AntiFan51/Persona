package com.AntiFan.persona.ui.screens.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.network.VolcEngineApi
import com.AntiFan.persona.data.network.model.ChatMessage
import com.AntiFan.persona.data.network.model.ChatRequest
import com.AntiFan.persona.data.network.model.ImageGenerationRequest
import com.AntiFan.persona.data.repository.IPersonaRepository
import com.AntiFan.persona.di.NetworkModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PersonaCreateViewModel @Inject constructor(
    private val repository: IPersonaRepository,
    private val api: VolcEngineApi
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _personality = MutableStateFlow("")
    val personality: StateFlow<String> = _personality.asStateFlow()

    private val _backstory = MutableStateFlow("")
    val backstory: StateFlow<String> = _backstory.asStateFlow()

    private val _avatarPath = MutableStateFlow("")
    val avatarPath: StateFlow<String> = _avatarPath.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    fun onNameChange(newValue: String) { _name.value = newValue }
    fun onPersonalityChange(newValue: String) { _personality.value = newValue }
    fun onBackstoryChange(newValue: String) { _backstory.value = newValue }

    /**
     * 🚀 旗舰版生成 V3：JSON解析 + 中文生图
     */
    fun generatePersonaByAI() {
        val currentName = _name.value
        if (currentName.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "正在构思设定..."
            try {
                val keywords = if (_personality.value.isEmpty()) "随机性格" else _personality.value

                // 1. 文本生成：要求返回 JSON 格式，且生图提示词用中文
                val textPrompt = """
                    任务：为虚拟角色“$currentName”生成设定（关键词：$keywords）。
                    
                    请直接返回一个标准的 JSON 格式内容，包含三个字段。
                    不要包含 markdown 标记（如 ```json），不要包含编号（如 1. 2.）。
                    
                    {
                        "personality": "这里填性格，简练，不要编号",
                        "backstory": "这里填背景故事，100字左右",
                        "image_prompt": "这里填用于生成头像的【中文】画面描述，描述外貌、五官、发型、光影、风格（如赛博朋克、二次元、写实）"
                    }
                """.trimIndent()

                val textResponse = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = ChatRequest(
                        model = NetworkModule.ENDPOINT_ID,
                        messages = listOf(ChatMessage(role = "user", content = textPrompt))
                    )
                )

                var aiContent = textResponse.choices.firstOrNull()?.message?.content ?: ""

                // 打印日志方便调试
                Log.d("PersonaCreate", "AI返回内容: $aiContent")

                // 2. 强力解析：使用正则提取 JSON 字段，无视 AI 的乱加格式
                val personality = extractJsonValue(aiContent, "personality")
                val backstory = extractJsonValue(aiContent, "backstory")
                val imagePrompt = extractJsonValue(aiContent, "image_prompt")

                if (personality.isNotEmpty()) _personality.value = personality
                if (backstory.isNotEmpty()) _backstory.value = backstory

                if (imagePrompt.isNotEmpty()) {
                    // 3. 图片生成：直接用中文 Prompt
                    _statusMessage.value = "正在绘制头像(中文指令)..."

                    val imageResponse = api.generateImage(
                        authorization = "Bearer ${NetworkModule.API_KEY}",
                        request = ImageGenerationRequest(
                            model = NetworkModule.CV_ENDPOINT_ID,
                            prompt = imagePrompt // 直接传中文
                        )
                    )

                    val url = imageResponse.data.firstOrNull()?.url
                    if (url != null) {
                        _avatarPath.value = url
                        _statusMessage.value = "生成完成！"
                    } else {
                        _statusMessage.value = "生图接口返回空数据"
                    }
                } else {
                    _statusMessage.value = "AI未返回画面描述，仅生成文本"
                }

            } catch (e: Exception) {
                Log.e("PersonaCreate", "Error", e)
                _statusMessage.value = "出错: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 🛠️ 正则提取工具：哪怕 AI 返回格式再乱，只要有 "key": "value" 就能抓出来
    private fun extractJsonValue(json: String, key: String): String {
        try {
            // 匹配 "key": "..." 或 "key" : "..."，支持换行
            val regex = "\"$key\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            val matchResult = regex.find(json)
            return matchResult?.groupValues?.get(1)?.trim() ?: ""
        } catch (e: Exception) {
            return ""
        }
    }

    fun savePersona(onSuccess: () -> Unit) {
        if (_name.value.isBlank()) return

        val finalUrl = if (_avatarPath.value.isNotEmpty()) _avatarPath.value else "https://picsum.photos/200"

        val newPersona = Persona(
            id = UUID.randomUUID().toString(),
            name = _name.value,
            personality = _personality.value,
            backstory = _backstory.value,
            avatarUrl = finalUrl,
            creatorId = "local_user"
        )

        viewModelScope.launch {
            repository.addPersona(newPersona)
            onSuccess()
        }
    }
}
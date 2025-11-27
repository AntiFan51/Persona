package com.AntiFan.persona.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.network.VolcEngineApi
import com.AntiFan.persona.data.network.model.ChatMessage
import com.AntiFan.persona.data.network.model.ChatRequest
import com.AntiFan.persona.data.network.model.ImageGenerationRequest
import com.AntiFan.persona.data.repository.IPersonaRepository
import com.AntiFan.persona.di.NetworkModule
import com.AntiFan.persona.ui.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PersonaDetailViewModel @Inject constructor(
    private val repository: IPersonaRepository,
    private val api: VolcEngineApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _persona = MutableStateFlow<Persona?>(null)
    val persona: StateFlow<Persona?> = _persona.asStateFlow()

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess.asStateFlow()

    init {
        val personaId: String? = savedStateHandle.get(AppDestinations.PERSONA_ID_KEY)
        if (personaId != null) {
            viewModelScope.launch {
                _persona.value = repository.getPersonaById(personaId)
            }
        }
    }

    fun consumeSuccessEvent() {
        _postSuccess.value = false
    }

    /**
     * 🔥 旗舰版发帖：图文并茂
     */
    fun triggerPersonaPost() {
        val currentPersona = _persona.value ?: return
        if (_isPosting.value) return

        viewModelScope.launch {
            _isPosting.value = true
            try {
                // 1. 构造 Prompt：要求 JSON 格式，包含文案和配图灵感
                val prompt = """
                    你现在是${currentPersona.name}。
                    性格：${currentPersona.personality}。
                    
                    任务：发一条带配图的朋友圈/动态。
                    
                    请返回标准 JSON 格式（不要 Markdown，不要编号）：
                    {
                        "content": "这里写文案，100字以内，符合人设语气",
                        "image_prompt": "这里写配图的【中文】画面描述，描述场景、光影、氛围，用于AI绘画"
                    }
                """.trimIndent()

                // 2. 调用文本模型
                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = listOf(ChatMessage(role = "user", content = prompt))
                )

                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )

                val aiRaw = response.choices.firstOrNull()?.message?.content ?: ""

                // 3. 解析 JSON
                val content = extractJsonValue(aiRaw, "content")
                val imagePrompt = extractJsonValue(aiRaw, "image_prompt")

                var finalImageUrl: String? = null

                // 4. 如果有画面描述，调用生图模型
                if (imagePrompt.isNotBlank()) {
                    try {
                        val imageReq = ImageGenerationRequest(
                            model = NetworkModule.CV_ENDPOINT_ID,
                            prompt = imagePrompt
                        )
                        val imageResp = api.generateImage(
                            authorization = "Bearer ${NetworkModule.API_KEY}",
                            request = imageReq
                        )
                        finalImageUrl = imageResp.data.firstOrNull()?.url
                    } catch (e: Exception) {
                        e.printStackTrace() // 生图失败不影响发帖，只是没图而已
                    }
                }

                // 5. 存入数据库 (content 不能为空，如果解析失败就用原始返回兜底)
                val finalContent = if (content.isNotBlank()) content else aiRaw

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    authorId = currentPersona.id,
                    content = finalContent,
                    imageUrl = finalImageUrl, // ✅ 存入图片 URL
                    likeCount = 0
                )
                repository.publishPost(newPost)

                _postSuccess.value = true

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPosting.value = false
            }
        }
    }

    // JSON 提取工具
    private fun extractJsonValue(json: String, key: String): String {
        try {
            val regex = "\"$key\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            val matchResult = regex.find(json)
            return matchResult?.groupValues?.get(1)?.trim() ?: ""
        } catch (e: Exception) {
            return ""
        }
    }
}
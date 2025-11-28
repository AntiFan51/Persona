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
import com.AntiFan.persona.data.manager.UserManager

@HiltViewModel
class PersonaDetailViewModel @Inject constructor(
    private val repository: IPersonaRepository,
    private val api: VolcEngineApi,
    private val userManager: UserManager,
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

    private val _isOwner = MutableStateFlow(false)
    val isOwner: StateFlow<Boolean> = _isOwner.asStateFlow()

    init {
        val personaId: String? = savedStateHandle.get(AppDestinations.PERSONA_ID_KEY)
        if (personaId != null) {
            viewModelScope.launch {
                val p = repository.getPersonaById(personaId)
                _persona.value = p

                // ✅ 2. 权限判断逻辑
                // 如果角色的 creatorId 等于 当前登录用户的 ID，那就是主人
                // 注意：兼容旧数据 "local_user"，假设 user_1 继承旧数据
                val currentUid = userManager.getCurrentUserId()
                if (p != null) {
                    _isOwner.value = (p.creatorId == currentUid) || (p.creatorId == "local_user" && currentUid == "user_1")
                }
            }
        }
    }

    fun consumeSuccessEvent() {
        _postSuccess.value = false
    }

    // --- 发帖逻辑 (保持不变) ---
    fun triggerPersonaPost() {
        val currentPersona = _persona.value ?: return
        if (_isPosting.value) return

        viewModelScope.launch {
            _isPosting.value = true
            try {
                val prompt = """
                    你现在是${currentPersona.name}。
                    性格：${currentPersona.personality}。
                    任务：发一条带配图的朋友圈/动态。
                    请返回标准 JSON 格式：
                    {"content": "文案", "image_prompt": "中文配图描述"}
                """.trimIndent()

                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = listOf(ChatMessage(role = "user", content = prompt))
                )
                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )
                val aiRaw = response.choices.firstOrNull()?.message?.content ?: ""

                val content = extractJsonValue(aiRaw, "content")
                val imagePrompt = extractJsonValue(aiRaw, "image_prompt")

                var finalImageUrl: String? = null
                if (imagePrompt.isNotBlank()) {
                    try {
                        val imageResp = api.generateImage(
                            authorization = "Bearer ${NetworkModule.API_KEY}",
                            request = ImageGenerationRequest(model = NetworkModule.CV_ENDPOINT_ID, prompt = imagePrompt)
                        )
                        finalImageUrl = imageResp.data.firstOrNull()?.url
                    } catch (e: Exception) { e.printStackTrace() }
                }

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    authorId = currentPersona.id,
                    content = if (content.isNotBlank()) content else aiRaw,
                    imageUrl = finalImageUrl,
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

    private fun extractJsonValue(json: String, key: String): String {
        return try {
            val regex = "\"$key\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            regex.find(json)?.groupValues?.get(1)?.trim() ?: ""
        } catch (e: Exception) { "" }
    }

    // ✅ 新增：删除当前角色
    fun deletePersona(onDeleted: () -> Unit) {
        val currentId = _persona.value?.id ?: return
        viewModelScope.launch {
            // 执行级联删除
            repository.deletePersonaRecursively(currentId)
            // 回调通知 UI 跳转
            onDeleted()
        }
    }
}
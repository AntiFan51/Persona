package com.AntiFan.persona.ui.screens.detail

import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.network.VolcEngineApi // 👈 新增
import com.AntiFan.persona.data.network.model.ChatMessage // 👈 新增
import com.AntiFan.persona.data.network.model.ChatRequest // 👈 新增
import com.AntiFan.persona.data.repository.IPersonaRepository
import com.AntiFan.persona.di.NetworkModule // 👈 新增
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
    private val api: VolcEngineApi, // 👈 1. 注入 API，这样才能调用 AI
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _persona = MutableStateFlow<Persona?>(null)
    val persona: StateFlow<Persona?> = _persona.asStateFlow()

    // 新增：正在发帖的状态（用于显示转圈圈）
    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    // 新增：发帖成功的信号（用于 UI 弹 Toast）
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

    // 重置成功状态（防止反复弹 Toast）
    fun consumeSuccessEvent() {
        _postSuccess.value = false
    }

    /**
     * 🔥 核心功能：让当前这个角色发一条动态
     */
    fun triggerPersonaPost() {
        val currentPersona = _persona.value ?: return
        if (_isPosting.value) return // 防止狂点

        viewModelScope.launch {
            _isPosting.value = true
            try {
                // 1. 构造 Prompt
                val prompt = """
                    你现在是${currentPersona.name}。
                    你的性格是：${currentPersona.personality}。
                    请发一条朋友圈/社交动态，分享你现在的心情。
                    要求：
                    1. 语气必须完全符合人设。
                    2. 字数在 100 字以内。
                    3. 直接输出内容，不要带引号。
                """.trimIndent()

                // 2. 调用 AI
                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = listOf(ChatMessage(role = "user", content = prompt))
                )

                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )

                val aiContent = response.choices.firstOrNull()?.message?.content ?: "..."

                // 3. 存入数据库
                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    authorId = currentPersona.id, // ✅ 指定作者是当前角色
                    content = aiContent,
                    likeCount = (0..50).random()
                )
                repository.publishPost(newPost)

                // 4. 标记成功
                _postSuccess.value = true

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPosting.value = false
            }
        }
    }
}
package com.AntiFan.persona.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.UiMessage
import com.AntiFan.persona.data.network.VolcEngineApi
import com.AntiFan.persona.data.network.model.ChatMessage
import com.AntiFan.persona.data.network.model.ChatRequest
import com.AntiFan.persona.data.repository.IPersonaRepository
import com.AntiFan.persona.di.NetworkModule
import com.AntiFan.persona.ui.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: IPersonaRepository,
    private val api: VolcEngineApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 当前聊天的角色
    private val _currentPersona = MutableStateFlow<Persona?>(null)
    val currentPersona: StateFlow<Persona?> = _currentPersona.asStateFlow()

    // 消息列表
    private val _messages = MutableStateFlow<List<UiMessage>>(emptyList())
    val messages: StateFlow<List<UiMessage>> = _messages.asStateFlow()

    // 是否正在“对方正在输入...”
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // 输入框的文本
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    init {
        // 1. 获取角色 ID 并加载角色数据
        val personaId: String? = savedStateHandle.get(AppDestinations.PERSONA_ID_KEY)
        if (personaId != null) {
            viewModelScope.launch {
                _currentPersona.value = repository.getPersonaById(personaId)

                // 加载成功后，可以让 AI 先打个招呼（可选，这里先留空）
            }
        }
    }

    // 更新输入框内容
    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    // 发送消息
    fun sendMessage() {
        val text = _inputText.value.trim()
        val persona = _currentPersona.value ?: return // 如果角色还没加载好，不能发
        if (text.isEmpty()) return

        // 1. 立即清空输入框
        _inputText.value = ""

        // 2. 把用户的消息添加到列表 (显示在右边)
        val userMsg = UiMessage(content = text, isUser = true)
        _messages.value = _messages.value + userMsg

        // 3. 调用 AI
        viewModelScope.launch {
            _isTyping.value = true
            try {
                // --- 构建 Prompt (核心) ---
                // 我们需要告诉 AI：“你现在不是 AI，你是[名字]，你的性格是...，背景是...”
                val systemPrompt = """
                    你现在通过扮演一个角色与我对话。
                    你的名字是：${persona.name}
                    你的性格是：${persona.personality}
                    你的背景故事是：${persona.backstory}
                    请完全沉浸在角色中，用符合你人设的语气和我聊天。不要暴露你是 AI。
                    回复要简短有力，像真人聊微信一样。
                """.trimIndent()

                // 组装历史消息（为了让 AI 记得上下文，简单起见，我们把最近 5 条聊天记录也发过去）
                // 这里的逻辑稍微复杂一点，我们先只发 System + 当前这一句，保证先跑通
                val apiMessages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user", content = text)
                )

                // 发起请求
                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = apiMessages
                )

                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )

                // 4. 获取 AI 回复并添加到列表 (显示在左边)
                val aiContent = response.choices.firstOrNull()?.message?.content ?: "..."
                val aiMsg = UiMessage(content = aiContent, isUser = false)
                _messages.value = _messages.value + aiMsg

            } catch (e: Exception) {
                // 错误处理：显示一个红色的错误消息
                val errorMsg = UiMessage(content = "连接失败: ${e.message}", isUser = false)
                _messages.value = _messages.value + errorMsg
            } finally {
                _isTyping.value = false
            }
        }
    }
}
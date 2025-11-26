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

    private val _currentPersona = MutableStateFlow<Persona?>(null)
    val currentPersona: StateFlow<Persona?> = _currentPersona.asStateFlow()

    private val _messages = MutableStateFlow<List<UiMessage>>(emptyList())
    val messages: StateFlow<List<UiMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _isEvolving = MutableStateFlow(false)
    val isEvolving: StateFlow<Boolean> = _isEvolving.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    init {
        val personaId: String? = savedStateHandle.get(AppDestinations.PERSONA_ID_KEY)
        if (personaId != null) {
            loadPersona(personaId)
        }
    }

    private fun loadPersona(id: String) {
        viewModelScope.launch {
            _currentPersona.value = repository.getPersonaById(id)
        }
    }

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        val persona = _currentPersona.value ?: return
        if (text.isEmpty()) return

        _inputText.value = ""
        val userMsg = UiMessage(content = text, isUser = true)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isTyping.value = true
            try {
                val systemPrompt = """
                    你现在是${persona.name}。
                    你的性格是：${persona.personality}。
                    你的背景是：${persona.backstory}。
                    请沉浸在角色中回复我，不要跳出人设。回复简短一点。
                """.trimIndent()

                val apiMessages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user", content = text)
                )

                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = apiMessages
                )

                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )

                val aiContent = response.choices.firstOrNull()?.message?.content ?: "..."
                val aiMsg = UiMessage(content = aiContent, isUser = false)
                _messages.value = _messages.value + aiMsg

            } catch (e: Exception) {
                val errorMsg = UiMessage(content = "连接失败: ${e.message}", isUser = false)
                _messages.value = _messages.value + errorMsg
            } finally {
                _isTyping.value = false
            }
        }
    }

    // --- 🔥 核心修复：共生进化逻辑 V5.0 ---
    fun triggerEvolution() {
        val persona = _currentPersona.value ?: return
        val chatHistory = _messages.value

        if (chatHistory.isEmpty()) {
            _toastMessage.value = "还没有聊天记录，无法进化"
            return
        }

        viewModelScope.launch {
            _isEvolving.value = true
            try {
                // 1. 关键策略：只提取用户的指令，完全忽略 AI 的回复
                // 这样总结模型就不会被 AI 的反驳带跑偏了
                val userOrders = chatHistory.filter { it.isUser }
                    .takeLast(10)
                    .joinToString("\n") { "用户指令: ${it.content}" }

                // 2. Prompt：改成填空题模式
                val prompt = """
                    你是一个角色设定更新器。
                    
                    旧设定：
                    [性格] ${persona.personality}
                    [背景] ${persona.backstory}
                    
                    用户发出的修改指令（必须强制执行）：
                    $userOrders
                    
                    请根据用户指令，生成新的设定。
                    
                    ⚠️ 内容创作要求（非常重要）：
                    1. **拒绝流水账**：背景故事不要写成“我叫XXX，我喜欢YYY”这种小学生造句。
                    2. **文学润色**：请用**第一人称独白**或**生动的人物侧写**来描述背景。要体现角色的语气和个性（比如傲娇、活泼或深沉）。
                    3. **自然融合**：将“新身份/新喜好”自然地融入故事中，而不是生硬地罗列。
                    
                    ⚠️ 输出格式要求：
                    第一行：新性格关键词（用逗号分隔）
                    第二行：新背景故事（100字以内，文笔要好）
                    （不要输出任何其他内容，只输出这两行）
                """.trimIndent()

                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = listOf(ChatMessage(role = "user", content = prompt))
                )

                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )
                val result = response.choices.firstOrNull()?.message?.content ?: ""

                // 打印日志，确保能看到（Tag: EVOLUTION）
                android.util.Log.d("EVOLUTION", "AI返回: $result")

                // 3. 解析逻辑：按行分割
                val lines = result.trim().lines().filter { it.isNotBlank() }

                if (lines.size >= 2) {
                    // 哪怕前面有 "性格：" 这种前缀，我们用 replace 去掉
                    val newPersonality = lines[0].replace("性格：", "").replace("新性格：", "").trim()
                    val newBackstory = lines[1].replace("背景：", "").replace("新背景：", "").trim()

                    repository.updatePersonaDetails(persona.id, newPersonality, newBackstory)
                    loadPersona(persona.id)
                    _toastMessage.value = "✨ 进化成功！设定已更新"
                } else {
                    // 兜底：如果 AI 只回了一行，就全塞进背景里
                    repository.updatePersonaDetails(persona.id, persona.personality, result)
                    _toastMessage.value = "✨ 设定部分更新 (AI 格式非常规)"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _toastMessage.value = "错误：${e.message}"
            } finally {
                _isEvolving.value = false
            }
        }
    }
}
package com.AntiFan.persona.ui.screens.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.network.VolcEngineApi
import com.AntiFan.persona.data.network.model.ChatMessage
import com.AntiFan.persona.data.network.model.ChatRequest
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
    private val repository: IPersonaRepository, // 依赖接口
    private val api: VolcEngineApi // 注入网络接口
) : ViewModel() {

    // 表单状态
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    // 这里虽然叫 personality，但在 AI 生成场景下，它充当“关键词”的角色
    private val _personality = MutableStateFlow("")
    val personality: StateFlow<String> = _personality.asStateFlow()

    private val _backstory = MutableStateFlow("")
    val backstory: StateFlow<String> = _backstory.asStateFlow()

    // 新增：加载状态，用于控制 UI 上的转圈圈
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 绑定输入框的回调函数
    fun onNameChange(newValue: String) { _name.value = newValue }
    fun onPersonalityChange(newValue: String) { _personality.value = newValue }
    fun onBackstoryChange(newValue: String) { _backstory.value = newValue }

    /**
     * 核心功能：调用 AI 生成设定
     */
    fun generatePersonaByAI() {
        val currentName = _name.value
        // 如果没填名字，就不执行
        if (currentName.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true // 开始加载
            try {
                // 1. 准备 Prompt (提示词)
                // 我们把用户输入的 personality 当作关键词发给 AI
                val keywords = _personality.value.ifBlank { "随机性格" }

                val prompt = """
                    我正在创建一个虚拟角色，名字叫“$currentName”，关键词是“$keywords”。
                    请帮我完善这个角色的设定。
                    请直接返回两部分内容，中间用“|||”分隔：
                    第一部分：简短的性格描述（50字以内）；
                    第二部分：精彩的背景故事（100字左右）。
                    例如：冷酷、高智商、黑客|||他出生在贫民窟，靠自学代码成为了顶尖黑客...
                    请严格按照格式返回，不要包含其他废话。
                """.trimIndent()

                // 2. 构建请求对象
                // 使用我们在 NetworkModule 里硬编码的 ENDPOINT_ID
                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = listOf(
                        ChatMessage(role = "user", content = prompt)
                    )
                )

                // 3. 发起网络请求
                // 使用我们在 NetworkModule 里硬编码的 API_KEY
                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )

                // 4. 解析结果
                val aiContent = response.choices.firstOrNull()?.message?.content ?: ""

                // 5. 简单的文本处理 (根据 ||| 分割)
                val parts = aiContent.split("|||")
                if (parts.size >= 2) {
                    _personality.value = parts[0].trim() // 填入性格
                    _backstory.value = parts[1].trim()   // 填入背景
                } else {
                    // 如果 AI 没按格式返回，就全填到背景里
                    _backstory.value = aiContent
                }

            } catch (e: Exception) {
                Log.e("PersonaCreateViewModel", "AI 生成失败", e)
                _backstory.value = "AI 连接失败: ${e.message}。请检查网络或 Key。"
            } finally {
                _isLoading.value = false // 结束加载
            }
        }
    }

    /**
     * 保存 Persona 的逻辑
     */
    fun savePersona(onSuccess: () -> Unit) {
        if (_name.value.isBlank() || _personality.value.isBlank()) return

        val newPersona = Persona(
            id = UUID.randomUUID().toString(),
            name = _name.value,
            personality = _personality.value,
            backstory = _backstory.value,
            avatarUrl = "https://picsum.photos/seed/${UUID.randomUUID()}/200",
            creatorId = "local_user"
        )

        // ✅ 关键修改：启动协程来执行数据库操作
        viewModelScope.launch {
            repository.addPersona(newPersona)
            // 保存完之后，再回调通知 UI 跳转
            onSuccess()
        }
    }
}
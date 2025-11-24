package com.AntiFan.persona.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor
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
class SocialViewModel @Inject constructor(
    private val repository: IPersonaRepository,
    private val api: VolcEngineApi
) : ViewModel() {

    // 广场上的动态列表
    private val _feed = MutableStateFlow<List<PostWithAuthor>>(emptyList())
    val feed: StateFlow<List<PostWithAuthor>> = _feed.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 初始化时加载数据
    init {
        loadFeed()
    }

    /**
     * 刷新广场列表
     */
    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _feed.value = repository.getSocialFeed()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🔥 核心功能：随机触发一个 AI 发帖
     * 这对应了需求文档中“由用户触发生成图文动态”
     */
    fun triggerAiPost() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. 先获取所有的角色
                val allPersonas = repository.getAllPersonas()
                if (allPersonas.isEmpty()) return@launch // 没人就没法发帖

                // 2. 随机选一个倒霉蛋...啊不，幸运儿
                val randomPersona = allPersonas.random()

                // 3. 让 AI 生成文案
                val prompt = """
                    你现在是${randomPersona.name}。
                    你的性格是：${randomPersona.personality}。
                    请发一条朋友圈/社交动态，分享你现在的心情或正在做的事。
                    要求：
                    1. 语气必须完全符合人设。
                    2. 字数在 100 字以内。
                    3. 不要带引号，直接输出内容。
                """.trimIndent()

                val request = ChatRequest(
                    model = NetworkModule.ENDPOINT_ID,
                    messages = listOf(ChatMessage(role = "user", content = prompt))
                )

                val response = api.chatCompletions(
                    authorization = "Bearer ${NetworkModule.API_KEY}",
                    request = request
                )

                val aiContent = response.choices.firstOrNull()?.message?.content ?: "..."

                // 4. 保存到数据库
                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    authorId = randomPersona.id,
                    content = aiContent,
                    likeCount = (0..100).random() // 随机点赞数，模拟人气
                )
                repository.publishPost(newPost)

                // 5. 刷新列表，让用户立刻看到
                loadFeed()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
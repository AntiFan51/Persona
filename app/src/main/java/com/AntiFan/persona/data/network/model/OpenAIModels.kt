package com.AntiFan.persona.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 1. 请求体
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

// 2. 消息体
@Serializable
data class ChatMessage(
    val role: String, // "user", "system", "assistant"
    val content: String
)

// 3. 响应体
@Serializable
data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val index: Int,
    val message: ChatMessage
)
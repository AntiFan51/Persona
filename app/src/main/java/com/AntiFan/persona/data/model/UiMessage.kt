package com.AntiFan.persona.data.model

import java.util.UUID

/**
 * 专门用于 UI 显示的聊天消息模型
 * (暂时不存数据库，仅在内存中运行)
 */
data class UiMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean, // true = 用户(右边), false = AI(左边)
    val timestamp: Long = System.currentTimeMillis()
)
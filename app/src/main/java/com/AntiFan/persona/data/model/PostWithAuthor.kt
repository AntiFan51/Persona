package com.AntiFan.persona.data.model

import androidx.room.Embedded

/**
 * 这是一个包含“帖子”和“作者信息”的组合类
 * Room 会自动把查出来的结果填进去，方便 UI 直接用
 */
data class PostWithAuthor(
    @Embedded val post: Post, // 帖子信息

    // 这里对应的就是作者的名字和头像
    val authorName: String,
    val authorAvatar: String
)
package com.AntiFan.persona.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithAuthor(
    @Embedded val post: Post,

    val authorName: String,
    val authorAvatar: String,

    // ✅ 新增：映射作者的关注状态
    val authorIsFollowed: Boolean
)
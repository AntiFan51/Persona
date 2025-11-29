package com.AntiFan.persona.data.model

import androidx.room.Embedded

data class PostWithAuthor(
    @Embedded val post: Post,

    val authorName: String,
    val authorAvatar: String,

    // ✅ 这两个字段现在通过 SQL 动态计算 (0=false, 1=true)
    val isLiked: Boolean,
    val authorIsFollowed: Boolean
)
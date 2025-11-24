package com.AntiFan.persona.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "posts")
@Serializable
data class Post(
    @PrimaryKey val id: String,
    val authorId: String,
    val content: String,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),

    // ✅ 新增：是否已点赞
    val isLiked: Boolean = false
)
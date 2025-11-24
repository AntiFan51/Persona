package com.AntiFan.persona.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "posts")
@Serializable
data class Post(
    @PrimaryKey val id: String,
    val authorId: String,   // 发帖人的 ID (外键)
    val content: String,    // 帖子内容
    val imageUrl: String? = null, // 配图 (可选)
    val likeCount: Int = 0, // 点赞数
    val timestamp: Long = System.currentTimeMillis() // 发布时间
)
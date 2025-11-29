package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor

interface IPersonaRepository {
    // --- 基础 ---
    suspend fun getAllPersonas(): List<Persona>
    suspend fun getPersonaById(id: String): Persona?
    suspend fun addPersona(persona: Persona)
    suspend fun deletePersonaRecursively(personaId: String)
    suspend fun updatePersonaDetails(id: String, personality: String, backstory: String)

    // --- 社交广场 (核心变动) ---
    // ✅ 变动：需要传入 userId，才能判断每条动态是否被【我】点赞过
    suspend fun getSocialFeed(currentUserId: String): List<PostWithAuthor>

    suspend fun publishPost(post: Post)

    // --- 互动操作 (核心变动) ---
    // ✅ 变动：需要传入 userId，记录是谁操作的
    suspend fun toggleFollow(userId: String, targetPersonaId: String, isFollowed: Boolean)
    suspend fun toggleLike(userId: String, postId: String, isLiked: Boolean)
}
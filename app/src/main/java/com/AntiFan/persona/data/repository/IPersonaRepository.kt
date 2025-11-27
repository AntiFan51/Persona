package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor

interface IPersonaRepository {
    // --- 角色基础操作 ---
    suspend fun getAllPersonas(): List<Persona>
    suspend fun getPersonaById(id: String): Persona?
    suspend fun addPersona(persona: Persona)

    // --- 社交操作 ---
    suspend fun toggleFollow(personaId: String, isFollowed: Boolean)
    suspend fun toggleLike(postId: String, isLiked: Boolean)

    // --- 广场内容 ---
    suspend fun getSocialFeed(): List<PostWithAuthor>
    suspend fun publishPost(post: Post)

    // ✅ 新增：更新角色设定 (共生进化)
    suspend fun updatePersonaDetails(id: String, personality: String, backstory: String)

    suspend fun deletePersonaRecursively(personaId: String)
}
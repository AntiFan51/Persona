package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor

interface IPersonaRepository {
    // 原有的 Persona 相关
    suspend fun getAllPersonas(): List<Persona>
    suspend fun getPersonaById(id: String): Persona?
    suspend fun addPersona(persona: Persona)

    // ✅ 新增：社交广场相关
    suspend fun getSocialFeed(): List<PostWithAuthor> // 获取所有动态
    suspend fun publishPost(post: Post)               // 发布一条动态

    suspend fun toggleFollow(personaId: String, isFollowed: Boolean)
    suspend fun toggleLike(postId: String, isLiked: Boolean)
}
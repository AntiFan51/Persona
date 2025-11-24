package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.local.PersonaDao
import com.AntiFan.persona.data.local.PostDao
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonaRepository @Inject constructor(
    private val personaDao: PersonaDao,
    private val postDao: PostDao // 👈 1. 注入 PostDao
) : IPersonaRepository {

    // --- Persona 部分 ---
    override suspend fun getAllPersonas(): List<Persona> {
        return personaDao.getAllPersonas()
    }

    override suspend fun getPersonaById(id: String): Persona? {
        return personaDao.getPersonaById(id)
    }

    override suspend fun addPersona(persona: Persona) {
        personaDao.insertPersona(persona)
    }

    // --- 社交广场部分 ---

    // 2. 获取广场列表
    override suspend fun getSocialFeed(): List<PostWithAuthor> {
        return postDao.getAllPosts()
    }

    // 3. 发布帖子
    override suspend fun publishPost(post: Post) {
        postDao.insertPost(post)
    }

    override suspend fun toggleFollow(personaId: String, isFollowed: Boolean) {
        personaDao.updateFollowStatus(personaId, isFollowed)
    }

    override suspend fun toggleLike(postId: String, isLiked: Boolean) {
        val delta = if (isLiked) 1 else -1
        postDao.updateLikeStatus(postId, isLiked, delta)
    }
}
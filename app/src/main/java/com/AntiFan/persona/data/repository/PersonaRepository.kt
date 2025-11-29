package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.local.PersonaDao
import com.AntiFan.persona.data.local.PostDao
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor
import com.AntiFan.persona.data.model.UserFollowRelation
import com.AntiFan.persona.data.model.UserLikeRelation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonaRepository @Inject constructor(
    private val personaDao: PersonaDao,
    private val postDao: PostDao
) : IPersonaRepository {

    override suspend fun getAllPersonas(): List<Persona> = personaDao.getAllPersonas()
    override suspend fun getPersonaById(id: String): Persona? = personaDao.getPersonaById(id)
    override suspend fun addPersona(persona: Persona) = personaDao.insertPersona(persona)

    override suspend fun deletePersonaRecursively(personaId: String) {
        postDao.deletePostsByAuthor(personaId)
        personaDao.deletePersonaById(personaId)
    }

    override suspend fun updatePersonaDetails(id: String, personality: String, backstory: String) {
        personaDao.updatePersonaDetails(id, personality, backstory)
    }

    // --- 社交广场 ---
    override suspend fun getSocialFeed(currentUserId: String): List<PostWithAuthor> {
        // ✅ 调用 DAO 的新查询，传入当前用户 ID
        return postDao.getSocialFeed(currentUserId)
    }

    override suspend fun publishPost(post: Post) {
        postDao.insertPost(post)
    }

    // --- 互动逻辑 (关键) ---

    override suspend fun toggleFollow(userId: String, targetPersonaId: String, isFollowed: Boolean) {
        if (isFollowed) {
            // 关注：插入关系
            personaDao.insertFollowRelation(UserFollowRelation(userId, targetPersonaId))
        } else {
            // 取消：删除关系
            personaDao.deleteFollowRelation(userId, targetPersonaId)
        }
    }

    override suspend fun toggleLike(userId: String, postId: String, isLiked: Boolean) {
        if (isLiked) {
            // 点赞：插入关系 + 计数加1
            postDao.insertLikeRelation(UserLikeRelation(userId, postId))
            postDao.updatePostLikeCount(postId, 1)
        } else {
            // 取消：删除关系 + 计数减1
            postDao.deleteLikeRelation(userId, postId)
            postDao.updatePostLikeCount(postId, -1)
        }
    }
}
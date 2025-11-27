package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor

@Dao
interface PostDao {
    // ✅ 这里的 SQL 必须正确映射 authorIsFollowed
    @Query("""
        SELECT 
            posts.*, 
            personas.name as authorName, 
            personas.avatarUrl as authorAvatar,
            personas.isFollowed as authorIsFollowed 
        FROM posts 
        INNER JOIN personas ON posts.authorId = personas.id 
        ORDER BY posts.timestamp DESC
    """)
    suspend fun getAllPosts(): List<PostWithAuthor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    // ✅ 新增：点赞状态更新
    @Query("UPDATE posts SET isLiked = :isLiked, likeCount = likeCount + :delta WHERE id = :id")
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, delta: Int)

    @Query("DELETE FROM posts WHERE authorId = :authorId")
    suspend fun deletePostsByAuthor(authorId: String)
}
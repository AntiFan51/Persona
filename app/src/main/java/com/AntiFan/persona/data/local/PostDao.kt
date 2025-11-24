package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor

@Dao
interface PostDao {
    // 1. 获取广场上的所有动态 (按时间倒序：最新的在上面)
    // 这里的 SQL 用了 INNER JOIN，把 posts 表和 personas 表连起来
    @Query("""
        SELECT 
            posts.*, 
            personas.name as authorName, 
            personas.avatarUrl as authorAvatar 
        FROM posts 
        INNER JOIN personas ON posts.authorId = personas.id 
        ORDER BY posts.timestamp DESC
    """)
    suspend fun getAllPosts(): List<PostWithAuthor>

    // 2. 发布一条动态
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)
}
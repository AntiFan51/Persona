package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.PostWithAuthor
import com.AntiFan.persona.data.model.UserLikeRelation

@Dao
interface PostDao {

    // ✅ 核心查询升级：传入 currentUserId
    // 使用子查询 (SELECT COUNT(*)...) > 0 来动态判断 true/false
    @Query("""
        SELECT 
            posts.*, 
            personas.name as authorName, 
            personas.avatarUrl as authorAvatar,
            (SELECT COUNT(*) FROM user_likes WHERE userId = :currentUserId AND targetPostId = posts.id) > 0 as isLiked,
            (SELECT COUNT(*) FROM user_follows WHERE userId = :currentUserId AND targetPersonaId = posts.authorId) > 0 as authorIsFollowed
        FROM posts 
        INNER JOIN personas ON posts.authorId = personas.id 
        ORDER BY posts.timestamp DESC
    """)
    suspend fun getSocialFeed(currentUserId: String): List<PostWithAuthor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    // --- 点赞关系操作 ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikeRelation(relation: UserLikeRelation)

    @Query("DELETE FROM user_likes WHERE userId = :userId AND targetPostId = :postId")
    suspend fun deleteLikeRelation(userId: String, postId: String)

    // 更新帖子表里的计数 (这是为了显示数字，虽然关系表也能算，但直接存数字读起来更快)
    @Query("UPDATE posts SET likeCount = likeCount + :delta WHERE id = :postId")
    suspend fun updatePostLikeCount(postId: String, delta: Int)

    // 级联删除：删人时，删掉相关的点赞记录
    @Query("DELETE FROM posts WHERE authorId = :authorId")
    suspend fun deletePostsByAuthor(authorId: String)
}
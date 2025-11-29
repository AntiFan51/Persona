package com.AntiFan.persona.data.model

import androidx.room.Entity

/**
 * 关注关系表
 * 联合主键：(userId, targetPersonaId) 确保用户不能重复关注同一个人
 */
@Entity(tableName = "user_follows", primaryKeys = ["userId", "targetPersonaId"])
data class UserFollowRelation(
    val userId: String,          // 谁点的关注 (当前登录用户)
    val targetPersonaId: String  // 关注了谁
)

/**
 * 点赞关系表
 * 联合主键：(userId, targetPostId) 确保用户对同一条帖子只能点赞一次
 */
@Entity(tableName = "user_likes", primaryKeys = ["userId", "targetPostId"])
data class UserLikeRelation(
    val userId: String,       // 谁点的赞 (当前登录用户)
    val targetPostId: String  // 点了哪条帖子
)
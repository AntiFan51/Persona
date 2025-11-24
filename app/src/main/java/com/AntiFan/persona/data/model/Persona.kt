package com.AntiFan.persona.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 修改说明：
 * 1. 新增 @Entity(tableName = "personas")：告诉 Room 这是一个数据库表，表名叫 "personas"。
 * 2. 新增 @PrimaryKey：告诉 Room 这个 id 字段是主键（唯一标识）。
 * 3. 保留 @Serializable：因为我们后面还是要解析 JSON 的，这个不能丢。
 */
@Entity(tableName = "personas")
@Serializable
data class Persona(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val personality: String,
    val backstory: String,
    val creatorId: String = "local_user"
)
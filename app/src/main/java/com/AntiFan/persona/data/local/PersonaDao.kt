package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Persona

@Dao
interface PersonaDao {
    // 获取所有角色
    @Query("SELECT * FROM personas")
    suspend fun getAllPersonas(): List<Persona>

    // 根据 ID 获取单个角色
    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun getPersonaById(id: String): Persona?

    // 插入或更新完整角色对象
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersona(persona: Persona)

    // 更新关注状态
    @Query("UPDATE personas SET isFollowed = :isFollowed WHERE id = :id")
    suspend fun updateFollowStatus(id: String, isFollowed: Boolean)

    // ✅ 新增：进化！更新角色的核心设定 (性格 + 背景)
    @Query("UPDATE personas SET personality = :personality, backstory = :backstory WHERE id = :id")
    suspend fun updatePersonaDetails(id: String, personality: String, backstory: String)
}
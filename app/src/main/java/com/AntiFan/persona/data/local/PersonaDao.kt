package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Persona

@Dao
interface PersonaDao {
    @Query("SELECT * FROM personas")
    suspend fun getAllPersonas(): List<Persona>

    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun getPersonaById(id: String): Persona?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersona(persona: Persona)

    // ✅ 新增：更新关注状态
    @Query("UPDATE personas SET isFollowed = :isFollowed WHERE id = :id")
    suspend fun updateFollowStatus(id: String, isFollowed: Boolean)
}
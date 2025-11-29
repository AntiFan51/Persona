package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.UserFollowRelation

@Dao
interface PersonaDao {
    @Query("SELECT * FROM personas")
    suspend fun getAllPersonas(): List<Persona>

    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun getPersonaById(id: String): Persona?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersona(persona: Persona)

    // --- 关注关系操作 ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowRelation(relation: UserFollowRelation)

    @Query("DELETE FROM user_follows WHERE userId = :userId AND targetPersonaId = :targetId")
    suspend fun deleteFollowRelation(userId: String, targetId: String)

    // --- 基础操作 ---

    @Query("DELETE FROM personas WHERE id = :id")
    suspend fun deletePersonaById(id: String)

    @Query("UPDATE personas SET personality = :personality, backstory = :backstory WHERE id = :id")
    suspend fun updatePersonaDetails(id: String, personality: String, backstory: String)
}
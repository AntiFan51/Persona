package com.AntiFan.persona.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.AntiFan.persona.data.model.Persona

@Dao
interface PersonaDao {
    // 1. 获取所有角色
    // 我们用 DESC (降序) 让最新创建的角色排在最前面
    @Query("SELECT * FROM personas")
    suspend fun getAllPersonas(): List<Persona>

    // 2. 根据 ID 查找角色
    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun getPersonaById(id: String): Persona?

    // 3. 插入或更新角色
    // OnConflictStrategy.REPLACE 意思是：如果 ID 一样，就覆盖旧的
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersona(persona: Persona)
}
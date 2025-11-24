package com.AntiFan.persona.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.AntiFan.persona.data.model.Persona

// 这里要把 Persona::class 放进去，version = 1 表示第一个版本
@Database(entities = [Persona::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
}
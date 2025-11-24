package com.AntiFan.persona.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post // 👈 导入 Post

// 1. entities 数组里加上 Post::class
// 2. version 改成 2 (因为数据库结构变了)
@Database(entities = [Persona::class, Post::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun postDao(): PostDao // 👈 加上这一行，暴露 PostDao
}
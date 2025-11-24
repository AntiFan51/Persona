package com.AntiFan.persona.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post

@Database(
    entities = [Persona::class, Post::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun postDao(): PostDao
}
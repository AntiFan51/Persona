package com.AntiFan.persona.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import com.AntiFan.persona.data.model.UserFollowRelation // 👈 新增
import com.AntiFan.persona.data.model.UserLikeRelation   // 👈 新增

@Database(
    entities = [
        Persona::class,
        Post::class,
        UserFollowRelation::class, // 注册
        UserLikeRelation::class    // 注册
    ],
    version = 4, // ✅ 升级到 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun postDao(): PostDao
}
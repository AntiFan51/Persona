package com.AntiFan.persona.di

import android.content.Context
import androidx.room.Room
import com.AntiFan.persona.data.local.AppDatabase
import com.AntiFan.persona.data.local.PersonaDao
import com.AntiFan.persona.data.local.PostDao // 👈 记得导入
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "persona_db"
        )
            .fallbackToDestructiveMigration() // 👈 建议加上这句：版本冲突时自动清空数据重建，防止开发时崩坏
            .build()
    }

    @Provides
    fun providePersonaDao(database: AppDatabase): PersonaDao {
        return database.personaDao()
    }

    // ✅ 新增：提供 PostDao
    @Provides
    fun providePostDao(database: AppDatabase): PostDao {
        return database.postDao()
    }
}
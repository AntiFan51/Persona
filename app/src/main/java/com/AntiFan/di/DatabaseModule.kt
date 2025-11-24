package com.AntiFan.di

import android.content.Context
import androidx.room.Room
import com.AntiFan.persona.data.local.AppDatabase
import com.AntiFan.persona.data.local.PersonaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 1. 提供数据库实例
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "persona_db" // 数据库文件的名字
        ).build()
    }

    // 2. 提供 DAO 实例 (这样 Repository 就可以直接注入 DAO 了)
    @Provides
    fun providePersonaDao(database: AppDatabase): PersonaDao {
        return database.personaDao()
    }
}
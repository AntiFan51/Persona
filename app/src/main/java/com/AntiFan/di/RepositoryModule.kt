package com.AntiFan.persona.di

import com.AntiFan.persona.data.repository.IPersonaRepository
import com.AntiFan.persona.data.repository.PersonaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPersonaRepository(
        personaRepository: PersonaRepository
    ): IPersonaRepository
}
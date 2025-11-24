package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.model.Persona

interface IPersonaRepository {
    // 👇 关键：这三个方法必须都要有 suspend
    suspend fun getAllPersonas(): List<Persona>

    suspend fun getPersonaById(id: String): Persona?

    suspend fun addPersona(persona: Persona)
}
package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.local.PersonaDao
import com.AntiFan.persona.data.model.Persona
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonaRepository @Inject constructor(
    private val dao: PersonaDao // 关键：这里注入了我们刚才定义的 DAO
) : IPersonaRepository {

    override suspend fun getAllPersonas(): List<Persona> {
        // 直接从数据库查
        return dao.getAllPersonas()
    }

    override suspend fun getPersonaById(id: String): Persona? {
        return dao.getPersonaById(id)
    }

    override suspend fun addPersona(persona: Persona) {
        // 插入到数据库
        dao.insertPersona(persona)
    }
}
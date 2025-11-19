package com.AntiFan.persona.data.repository

import com.AntiFan.persona.data.datasource.MockData
import com.AntiFan.persona.data.model.Persona
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository 负责管理数据来源。
 * 目前它从 MockData 获取数据，未来我们会在这里加入网络请求或数据库查询。
 */
@Singleton // 告诉 Hilt，这个类在整个应用中只创建一个实例
class PersonaRepository @Inject constructor() {

    // 根据 ID 查找 Persona
    fun getPersonaById(id: String): Persona? {
        return MockData.personas.find { it.id == id }
    }

    // 获取所有 Persona (给列表页用的，虽然目前列表页还没用 Repository，但我们可以预留)
    fun getAllPersonas(): List<Persona> {
        return MockData.personas
    }
}
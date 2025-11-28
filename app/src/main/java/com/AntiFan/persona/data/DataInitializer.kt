package com.AntiFan.persona.data

import com.AntiFan.persona.data.datasource.MockDataAssets
import com.AntiFan.persona.data.repository.IPersonaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val repository: IPersonaRepository
) {
    /**
     * 检查并初始化数据
     * 逻辑：如果数据库里一个人都没有，就写入预置数据
     */
    fun initData() {
        // 使用 IO 线程执行数据库操作
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentPersonas = repository.getAllPersonas()

                if (currentPersonas.isEmpty()) {
                    // 1. 写入角色
                    MockDataAssets.presetsPersonas.forEach { persona ->
                        repository.addPersona(persona)
                    }

                    // 2. 写入动态
                    MockDataAssets.presetPosts.forEach { post ->
                        repository.publishPost(post)
                    }

                    println("DataInitializer: 预置数据写入完成！")
                } else {
                    println("DataInitializer: 数据库已有数据，跳过初始化。")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
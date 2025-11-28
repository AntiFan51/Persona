package com.AntiFan.persona.data.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 全局用户管理器
 * 负责管理当前登录的用户身份
 */
@Singleton
class UserManager @Inject constructor() {

    // 默认登录用户 ID
    private val _currentUserId = MutableStateFlow("user_1")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    // 模拟的用户列表（方便切换）
    val availableUsers = listOf(
        User("user_1", "我 (User A)"),
        User("user_2", "马甲 (User B)"),
        User("user_3", "游客 (User C)")
    )

    fun switchUser(userId: String) {
        _currentUserId.value = userId
    }

    fun getCurrentUserId(): String = _currentUserId.value
}

data class User(val id: String, val name: String)
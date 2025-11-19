package com.AntiFan.persona.ui.screens.create

import androidx.lifecycle.ViewModel
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.repository.PersonaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PersonaCreateViewModel @Inject constructor(
    private val repository: PersonaRepository
) : ViewModel() {

    // 使用 StateFlow 管理输入框的状态，初始值为空字符串
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _personality = MutableStateFlow("")
    val personality: StateFlow<String> = _personality.asStateFlow()

    private val _backstory = MutableStateFlow("")
    val backstory: StateFlow<String> = _backstory.asStateFlow()

    // 绑定输入框的回调函数
    fun onNameChange(newValue: String) { _name.value = newValue }
    fun onPersonalityChange(newValue: String) { _personality.value = newValue }
    fun onBackstoryChange(newValue: String) { _backstory.value = newValue }

    /**
     * 保存 Persona 的逻辑
     * @param onSuccess 保存成功后的回调函数（通常用于导航返回）
     */
    fun savePersona(onSuccess: () -> Unit) {
        // 简单的非空校验
        if (_name.value.isBlank() || _personality.value.isBlank()) return

        // 创建一个新的 Persona 对象
        val newPersona = Persona(
            id = UUID.randomUUID().toString(),
            name = _name.value,
            personality = _personality.value,
            backstory = _backstory.value,
            // 暂时使用随机头像，模拟用户选择
            avatarUrl = "https://picsum.photos/seed/${UUID.randomUUID()}/200",
            creatorId = "local_user"
        )

        // 调用仓库保存
        repository.addPersona(newPersona)

        // 通知 UI 保存成功
        onSuccess()
    }
}
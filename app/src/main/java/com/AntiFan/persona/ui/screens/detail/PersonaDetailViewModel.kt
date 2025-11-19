package com.AntiFan.persona.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.repository.PersonaRepository
import com.AntiFan.persona.ui.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PersonaDetailViewModel @Inject constructor(
    private val repository: PersonaRepository,
    savedStateHandle: SavedStateHandle // Hilt 会自动把导航参数塞到这里面
) : ViewModel() {

    // UI 状态：当前显示的 Persona。初始值为 null。
    private val _persona = MutableStateFlow<Persona?>(null)
    val persona: StateFlow<Persona?> = _persona.asStateFlow()

    init {
        // 1. 从导航参数中自动获取 'personaId'
        // SavedStateHandle 就像一个地图，它自动保存了传过来的参数
        val personaId: String? = savedStateHandle.get(AppDestinations.PERSONA_ID_KEY)

        // 2. 如果 ID 存在，就去仓库里找对应的人
        if (personaId != null) {
            _persona.value = repository.getPersonaById(personaId)
        }
    }
}
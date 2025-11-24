package com.AntiFan.persona.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // 必须导入这个
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.repository.IPersonaRepository // 改用接口
import com.AntiFan.persona.ui.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch // 必须导入这个
import javax.inject.Inject

@HiltViewModel
class PersonaDetailViewModel @Inject constructor(
    private val repository: IPersonaRepository, // 建议：这里改用接口类型，保持统一
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _persona = MutableStateFlow<Persona?>(null)
    val persona: StateFlow<Persona?> = _persona.asStateFlow()

    init {
        val personaId: String? = savedStateHandle.get(AppDestinations.PERSONA_ID_KEY)

        if (personaId != null) {
            // ✅ 修复点：启动协程来获取数据
            viewModelScope.launch {
                _persona.value = repository.getPersonaById(personaId)
            }
        }
    }
}
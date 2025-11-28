package com.AntiFan.persona.ui.screens

import androidx.lifecycle.ViewModel
import com.AntiFan.persona.data.manager.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    val currentUser = userManager.currentUserId

    fun switchNextUser() {
        val current = userManager.getCurrentUserId()
        val next = if (current == "user_1") "user_2" else "user_1"
        userManager.switchUser(next)
    }
}
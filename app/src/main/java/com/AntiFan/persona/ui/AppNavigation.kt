package com.AntiFan.persona.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.AntiFan.persona.ui.screens.PersonaDetailScreen
import com.AntiFan.persona.ui.screens.PersonaListScreen
import com.AntiFan.persona.ui.screens.create.PersonaCreateScreen
import com.AntiFan.persona.ui.screens.chat.ChatScreen

object AppDestinations {
    const val PERSONA_LIST = "persona_list"
    const val PERSONA_DETAIL = "persona_detail"
    const val PERSONA_CREATE = "persona_create"
    const val PERSONA_ID_KEY = "personaId"

    // ✅ 新增：聊天页路由
    const val PERSONA_CHAT = "persona_chat"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.PERSONA_LIST
    ) {
        // 1. 列表页路由
        composable(route = AppDestinations.PERSONA_LIST) {
            PersonaListScreen(navController = navController)
        }

        // 2. 创作页路由
        composable(route = AppDestinations.PERSONA_CREATE) {
            PersonaCreateScreen(navController = navController)
        }

        // 3. 详情页路由
        // 路由格式: "persona_detail/{personaId}"
        composable(
            route = "${AppDestinations.PERSONA_DETAIL}/{${AppDestinations.PERSONA_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.PERSONA_ID_KEY) { type = NavType.StringType })
        ) {
            // ✅ 关键修改：将 navController 传递给 Screen
            // 这样 PersonaDetailScreen 里的 "开始对话" 按钮才能跳转
            PersonaDetailScreen(navController = navController)
        }

        // 4. 聊天页路由
        // 路由格式: "persona_chat/{personaId}"
        composable(
            route = "${AppDestinations.PERSONA_CHAT}/{${AppDestinations.PERSONA_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.PERSONA_ID_KEY) { type = NavType.StringType })
        ) {
            // 聊天页面
            ChatScreen(navController = navController)
        }
    }
}
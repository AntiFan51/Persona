package com.AntiFan.persona.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.AntiFan.persona.ui.screens.HomeScreen
import com.AntiFan.persona.ui.screens.PersonaDetailScreen
import com.AntiFan.persona.ui.screens.PersonaListScreen
import com.AntiFan.persona.ui.screens.chat.ChatScreen
import com.AntiFan.persona.ui.screens.create.PersonaCreateScreen

object AppDestinations {
    const val HOME = "home" // ✅ 1. 新增 HOME 路由
    const val PERSONA_LIST = "persona_list"
    const val PERSONA_DETAIL = "persona_detail"
    const val PERSONA_CREATE = "persona_create"
    const val PERSONA_ID_KEY = "personaId"
    const val PERSONA_CHAT = "persona_chat"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // ✅ 2. 修改启动页为 HOME
        startDestination = AppDestinations.HOME
    ) {
        // ✅ 3. 定义 HOME 路由
        composable(route = AppDestinations.HOME) {
            HomeScreen(navController = navController)
        }

        // --- 下面的路由保持不变，但 PERSONA_LIST 其实已经嵌入到 HomeScreen 里了 ---
        // 我们可以保留它，防止有其他地方跳转用到

        composable(route = AppDestinations.PERSONA_LIST) {
            PersonaListScreen(navController = navController)
        }

        composable(route = AppDestinations.PERSONA_CREATE) {
            PersonaCreateScreen(navController = navController)
        }

        composable(
            route = "${AppDestinations.PERSONA_DETAIL}/{${AppDestinations.PERSONA_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.PERSONA_ID_KEY) { type = NavType.StringType })
        ) {
            PersonaDetailScreen(navController = navController)
        }

        composable(
            route = "${AppDestinations.PERSONA_CHAT}/{${AppDestinations.PERSONA_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.PERSONA_ID_KEY) { type = NavType.StringType })
        ) {
            ChatScreen(navController = navController)
        }
    }
}
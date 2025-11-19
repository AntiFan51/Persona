package com.AntiFan.persona.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.AntiFan.persona.ui.screens.PersonaDetailScreen
import com.AntiFan.persona.ui.screens.PersonaListScreen

object AppDestinations {
    const val PERSONA_LIST = "persona_list"
    const val PERSONA_DETAIL = "persona_detail"
    const val PERSONA_ID_KEY = "personaId" // 定义参数名为常量
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
            // 【关键修改】这里我们将 navController 传给列表页，让它有能力发起跳转
            PersonaListScreen(navController = navController)
        }

        // 2. 详情页路由
        // 路由格式: "persona_detail/{personaId}"
        // ... 前面的代码不变 ...

        composable(
            route = "${AppDestinations.PERSONA_DETAIL}/{${AppDestinations.PERSONA_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.PERSONA_ID_KEY) { type = NavType.StringType })
        ) {
            // 【关键修改】
            // 我们不再需要在这里手动获取 personaId 并传给 Screen。
            // Screen 内部的 ViewModel 会自动通过 SavedStateHandle 读取这个参数。
            PersonaDetailScreen()
        }
    }
}
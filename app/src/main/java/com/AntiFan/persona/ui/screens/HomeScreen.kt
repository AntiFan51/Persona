package com.AntiFan.persona.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.AntiFan.persona.ui.screens.social.SocialScreen

@Composable
fun HomeScreen(navController: NavController) {
    // 记录当前选中的 Tab (0 = 广场, 1 = 通讯录)
    // 使用 rememberSaveable 确保旋转屏幕后 Tab 状态不丢失
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Tab 1: 广场
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "广场") },
                    label = { Text("广场") }
                )
                // Tab 2: 通讯录
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "通讯录") },
                    label = { Text("通讯录") }
                )
            }
        }
    ) { paddingValues ->
        // 根据选中的 Tab 显示不同的页面
        // Box 用于处理 padding，防止内容被底部导航栏遮挡
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> SocialScreen(navController = navController)
                1 -> PersonaListScreen(navController = navController)
            }
        }
    }
}
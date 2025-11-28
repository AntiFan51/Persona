package com.AntiFan.persona.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.AntiFan.persona.ui.screens.social.SocialScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    // ✅ 注入 ViewModel 用于管理用户状态
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 记录当前选中的 Tab (0 = 广场, 1 = 通讯录)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // 监听当前是谁在登录
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                // 显示更友好的用户名称
                title = {
                    val displayName = if (currentUser == "user_1") "我的大号 (User A)" else "我的马甲 (User B)"
                    Text("当前: $displayName", style = MaterialTheme.typography.titleMedium)
                },
                actions = {
                    // 切换用户按钮
                    IconButton(onClick = { viewModel.switchNextUser() }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Switch User")
                    }
                }
            )
        },
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
        // 内容区域
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> SocialScreen(navController = navController)
                1 -> PersonaListScreen(navController = navController)
            }
        }
    }
}
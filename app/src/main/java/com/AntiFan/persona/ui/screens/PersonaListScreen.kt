package com.AntiFan.persona.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.ui.AppDestinations
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage

/**
 * 角色列表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaListScreen(
    navController: NavController,
    viewModel: PersonaListViewModel = hiltViewModel()
) {
    // 监听 ViewModel 中的状态
    val personas by viewModel.personas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 每次进入页面时，自动刷新数据
    LaunchedEffect(Unit) {
        viewModel.loadPersonas()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Persona 列表") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 点击 + 号跳转到创建页面
                navController.navigate(AppDestinations.PERSONA_CREATE)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Create")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            if (isLoading) {
                // 加载中显示转圈
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (personas.isEmpty()) {
                // 空数据提示
                Text(
                    text = "还没有角色，点击 + 号创建一个吧！",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // 显示列表
                LazyColumn {
                    items(personas) { persona ->
                        PersonaItem(
                            persona = persona,
                            onClick = {
                                // 点击卡片跳转到详情页
                                navController.navigate("${AppDestinations.PERSONA_DETAIL}/${persona.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 单个角色的卡片组件
 * 必须定义在 PersonaListScreen 外面（或者作为独立函数）
 */
@Composable
fun PersonaItem(
    persona: Persona,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // 增加一点边距
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        // 使用 Row (横向布局)：左边头像，右边文字
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // 垂直居中
        ) {
            // 1. 头像组件 (Coil)
            AsyncImage(
                model = persona.avatarUrl, // 数据源
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(64.dp) // 头像大小
                    .clip(CircleShape) // 裁剪成圆形
            )

            Spacer(modifier = Modifier.width(16.dp)) // 头像和文字中间的空隙

            // 2. 文字区域
            Column(
                modifier = Modifier.weight(1f) // 占满剩余空间
            ) {
                Text(
                    text = persona.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = persona.personality,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2 // 最多显示2行
                )
            }
        }
    }
}
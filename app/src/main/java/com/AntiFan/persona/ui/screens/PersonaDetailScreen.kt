package com.AntiFan.persona.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.AntiFan.persona.ui.AppDestinations
import com.AntiFan.persona.ui.screens.detail.PersonaDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaDetailScreen(
    navController: NavController, // 👈 1. 新增：我们需要它来跳转
    viewModel: PersonaDetailViewModel = hiltViewModel()
) {
    val persona by viewModel.persona.collectAsState()

    Scaffold(
        // 2. 顶部栏（可选，方便返回）
        topBar = {
            TopAppBar(
                title = { Text("角色详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        // 3. 核心：右下角的“开始对话”按钮
        floatingActionButton = {
            // 只有当 persona 数据加载出来后，才显示按钮
            persona?.let { p ->
                ExtendedFloatingActionButton(
                    onClick = {
                        // 跳转到聊天页，带上 ID
                        navController.navigate("${AppDestinations.PERSONA_CHAT}/${p.id}")
                    },
                    icon = { Icon(Icons.Default.Send, contentDescription = null) },
                    text = { Text("开始对话") }
                )
            }
        }
    ) { paddingValues ->
        // 4. 内容区域
        Box(
            modifier = Modifier
                .padding(paddingValues) // 必须加上这个 padding
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            persona?.let { p ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 头像
                    Image(
                        painter = rememberAsyncImagePainter(p.avatarUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 名字
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 性格
                    Text(
                        text = "性格设定",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = p.personality,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 背景故事
                    Text(
                        text = "背景故事",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = p.backstory,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() // 换成转圈圈更好看
                }
            }
        }
    }
}
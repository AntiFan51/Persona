package com.AntiFan.persona.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit // 用个编辑图标表示发帖
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    navController: NavController,
    viewModel: PersonaDetailViewModel = hiltViewModel()
) {
    val persona by viewModel.persona.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState() // 监听是否正在生成
    val postSuccess by viewModel.postSuccess.collectAsState() // 监听是否成功

    val context = LocalContext.current

    // 监听发帖成功事件
    LaunchedEffect(postSuccess) {
        if (postSuccess) {
            Toast.makeText(context, "动态发布成功！已同步到广场", Toast.LENGTH_SHORT).show()
            viewModel.consumeSuccessEvent() // 消耗事件
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("角色详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                // ✅ 新增：右上角的发帖按钮
                actions = {
                    if (isPosting) {
                        // 如果正在生成，显示转圈圈
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        // 否则显示按钮
                        IconButton(onClick = { viewModel.triggerPersonaPost() }) {
                            Icon(Icons.Default.Edit, contentDescription = "让TA发帖")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            persona?.let { p ->
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("${AppDestinations.PERSONA_CHAT}/${p.id}")
                    },
                    icon = { Icon(Icons.Default.Send, contentDescription = null) },
                    text = { Text("开始对话") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
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
                    Image(
                        painter = rememberAsyncImagePainter(p.avatarUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "性格设定", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = p.personality, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "背景故事", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = p.backstory, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))

                    // 提示文字
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "💡 点击右上角图标，可以让 TA 发布一条动态哦",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
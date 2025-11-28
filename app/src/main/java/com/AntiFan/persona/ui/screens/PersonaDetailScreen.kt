package com.AntiFan.persona.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    // 监听数据
    val persona by viewModel.persona.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()

    // ✅ 监听权限状态：我是不是这个角色的主人？
    val isOwner by viewModel.isOwner.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 处理发帖成功提示
    LaunchedEffect(postSuccess) {
        if (postSuccess) {
            Toast.makeText(context, "动态发布成功！", Toast.LENGTH_SHORT).show()
            viewModel.consumeSuccessEvent()
        }
    }

    // 删除确认弹窗
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除？") },
            text = { Text("删除后，该角色及其发布的所有动态都将消失，且无法恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePersona {
                            Toast.makeText(context, "角色已删除", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("确认删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("角色详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // ✅ 权限控制核心：只有主人 (Owner) 才能看到管理按钮
                    if (isOwner) {
                        // 1. 发帖按钮
                        if (isPosting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp).padding(end = 16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { viewModel.triggerPersonaPost() }) {
                                Icon(Icons.Default.Edit, contentDescription = "发帖")
                            }
                        }

                        // 2. 删除按钮
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        // 如果不是主人，这里可以留空，或者显示一个“关注”按钮（目前关注在广场页，这里留空保持清爽）
                    }
                }
            )
        },
        floatingActionButton = {
            // “开始对话”按钮对所有人开放
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

                    // ✅ 显示创作者标识 (可选优化)
                    if (isOwner) {
                        Text(
                            text = "(由我创建)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 设定详情
                    Text(text = "性格设定", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = p.personality, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "背景故事", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = p.backstory, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))

                    // 提示文字 (仅对主人显示发帖提示)
                    if (isOwner) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "💡 点击右上角图标，可以让 TA 发布一条动态哦",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
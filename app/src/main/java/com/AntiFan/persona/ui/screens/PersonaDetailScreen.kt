package com.AntiFan.persona.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // 垃圾桶图标
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
    val persona by viewModel.persona.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()

    // ✅ 新增：控制删除确认弹窗的显示状态
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(postSuccess) {
        if (postSuccess) {
            Toast.makeText(context, "动态发布成功！", Toast.LENGTH_SHORT).show()
            viewModel.consumeSuccessEvent()
        }
    }

    // ✅ 删除确认弹窗
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除？") },
            text = { Text("删除后，该角色及其发布的所有动态都将消失，且无法恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        // 调用 ViewModel 删除，成功后返回上一页
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
                    // 1. 发帖按钮 (Loading 时转圈)
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

                    // ✅ 2. 新增：删除按钮
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error // 红色警告色
                        )
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
                        modifier = Modifier.size(120.dp).clip(CircleShape),
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
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
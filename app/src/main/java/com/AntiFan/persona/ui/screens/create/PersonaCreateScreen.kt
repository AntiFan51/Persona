package com.AntiFan.persona.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaCreateScreen(
    navController: NavController,
    viewModel: PersonaCreateViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val personality by viewModel.personality.collectAsState()
    val backstory by viewModel.backstory.collectAsState()

    // 监听头像 URL
    val avatarPath by viewModel.avatarPath.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建新角色") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. 头像预览区域 ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            ) {
                if (avatarPath.isNotBlank()) {
                    // 直接显示 URL
                    AsyncImage(
                        model = avatarPath,
                        contentDescription = "AI Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // 默认占位符
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("头像", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
                }
            }

            // 状态提示
            if (statusMessage.isNotBlank()) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            // --- 2. 名字输入 ---
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("名字 (必填)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // --- 3. AI 生成按钮 ---
            Button(
                onClick = { viewModel.generatePersonaByAI() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                if (isLoading) {
                    Text("AI 正在创作中...")
                } else {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI 生成设定 & 绘制头像")
                }
            }

            // 分割线
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- 4. 性格 & 背景 ---
            OutlinedTextField(
                value = personality,
                onValueChange = viewModel::onPersonalityChange,
                label = { Text("性格 / 关键词") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            OutlinedTextField(
                value = backstory,
                onValueChange = viewModel::onBackstoryChange,
                label = { Text("背景故事") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- 5. 保存按钮 ---
            Button(
                onClick = { viewModel.savePersona { navController.popBackStack() } },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && name.isNotBlank() && personality.isNotBlank()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("创建角色")
            }
        }
    }
}
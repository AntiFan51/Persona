package com.AntiFan.persona.ui.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaCreateScreen(
    navController: NavController,
    viewModel: PersonaCreateViewModel = hiltViewModel()
) {
    // 1. 收集 ViewModel 中的状态
    val name by viewModel.name.collectAsState()
    val personality by viewModel.personality.collectAsState() // 这里其实是 keywords
    val backstory by viewModel.backstory.collectAsState()

    // 2. 收集加载状态 (新增)
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建角色") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 基础信息
            Text(
                text = "基础设定",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("名字") },
                placeholder = { Text("给你的 Persona 起个名字") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 详细设定
            Text(
                text = "灵魂注入",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 提示用户输入关键词
            OutlinedTextField(
                value = personality,
                onValueChange = viewModel::onPersonalityChange,
                label = { Text("性格关键词") },
                placeholder = { Text("例如：傲娇、赛博朋克、侦探... (用于AI生成)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // 背景故事 (AI 生成的目标区域)
            OutlinedTextField(
                value = backstory,
                onValueChange = viewModel::onBackstoryChange,
                label = { Text("背景故事") },
                placeholder = { Text("点击下方按钮，AI 将为你自动撰写...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 8 // 稍微高一点，方便看生成的故事
            )

            // --- 核心按钮区域 ---

            // 1. AI 生成按钮
            FilledTonalButton(
                onClick = { viewModel.generatePersonaByAI() }, // 连接 ViewModel 函数
                modifier = Modifier.fillMaxWidth(),
                // 只有在没加载，且名字不为空时可用
                enabled = !isLoading && name.isNotBlank()
            ) {
                if (isLoading) {
                    // 加载时的 UI：转圈圈 + 文字
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("正在联络豆包大脑...")
                } else {
                    // 正常状态 UI
                    Text("✨ AI 一键生成设定")
                }
            }

            // 2. 保存按钮
            Button(
                onClick = {
                    viewModel.savePersona {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank()
            ) {
                Text("完成并创建")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
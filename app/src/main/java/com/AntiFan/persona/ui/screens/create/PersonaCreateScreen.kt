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
    // 收集 ViewModel 中的状态
    val name by viewModel.name.collectAsState()
    val personality by viewModel.personality.collectAsState()
    val backstory by viewModel.backstory.collectAsState()

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
        // 使用 Column 垂直排列表单项
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp) // 标准的水平边距 16dp
                .verticalScroll(rememberScrollState()), // 允许滚动
            verticalArrangement = Arrangement.spacedBy(24.dp) // 统一的大间距 24dp，让表单不拥挤
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. 基础信息板块
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

            // 2. 详细设定板块
            Text(
                text = "灵魂注入",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = personality,
                onValueChange = viewModel::onPersonalityChange,
                label = { Text("性格关键词") },
                placeholder = { Text("例如：傲娇、赛博朋克、爱吃甜食...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = backstory,
                onValueChange = viewModel::onBackstoryChange,
                label = { Text("背景故事") },
                placeholder = { Text("他/她来自哪里？有什么不为人知的过去？") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            // 3. 操作按钮
            // 这里的按钮我们暂时留空 AI 功能，稍后实现
            FilledTonalButton(
                onClick = { /* TODO: AI Generate */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✨ AI 一键生成设定 (开发中)")
            }

            Button(
                onClick = {
                    viewModel.savePersona {
                        navController.popBackStack() // 保存成功后返回列表
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // 只有名字和性格都不为空时，按钮才可用，这是很好的交互细节
                enabled = name.isNotBlank() && personality.isNotBlank()
            ) {
                Text("完成并创建")
            }

            Spacer(modifier = Modifier.height(32.dp)) // 底部留白
        }
    }
}
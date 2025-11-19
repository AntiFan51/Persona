package com.AntiFan.persona.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.AntiFan.persona.data.datasource.MockData
import com.AntiFan.persona.ui.AppDestinations
import com.AntiFan.persona.ui.components.PersonaCard

@Composable
fun PersonaListScreen(navController: NavController) {
    // 使用 Scaffold 来安放悬浮按钮 (FAB)
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // [New] 点击跳转到创作页面
                    navController.navigate(AppDestinations.PERSONA_CREATE)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Persona")
            }
        }
    ) { paddingValues ->
        // 列表内容
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // [重要] 防止列表被 Scaffold 的组件遮挡
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(MockData.personas) { persona ->
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate("${AppDestinations.PERSONA_DETAIL}/${persona.id}")
                    }
                ) {
                    PersonaCard(persona = persona)
                }
            }
        }
    }
}
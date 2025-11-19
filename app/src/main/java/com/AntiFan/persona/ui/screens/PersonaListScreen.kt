package com.AntiFan.persona.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // 别忘了导入这个！
import com.AntiFan.persona.data.datasource.MockData
import com.AntiFan.persona.ui.AppDestinations
import com.AntiFan.persona.ui.components.PersonaCard

@Composable
fun PersonaListScreen(navController: NavController) { // 1. 接收 navController 参数
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(MockData.personas) { persona ->
            // 2. 使用 Box 包裹 PersonaCard 并添加 clickable 修饰符
            Box(
                modifier = Modifier.clickable {
                    // 3. 点击时触发导航
                    // 目标路由: "persona_detail/具体的ID"
                    navController.navigate("${AppDestinations.PERSONA_DETAIL}/${persona.id}")
                }
            ) {
                PersonaCard(persona = persona)
            }
        }
    }
}
package com.AntiFan.persona // 请确保这个包名是正确的

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.AntiFan.persona.data.datasource.MockData
import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.ui.theme.PersonaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonaTheme {
                // Surface 是一个带有背景色的容器
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 调用我们的主屏幕 Composable
                    PersonaListScreen()
                }
            }
        }
    }
}

/**
 * 这是主屏幕的 Composable 函数，负责展示 Persona 列表。
 */
@Composable
fun PersonaListScreen() {
    // LazyColumn 是一个用于显示可滚动列表的高效组件。
    // 它非常高效，只会渲染屏幕上可见的列表项，适合展示长列表。
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // items 是 LazyColumn 的一个函数，可以方便地遍历一个列表来创建列表项。
        items(MockData.personas) { persona ->
            // 对列表中的每一个 persona，我们都创建一个 PersonaCard
            PersonaCard(persona = persona)
        }
    }
}

/**
 * 这是一个用于显示单个 Persona 信息的卡片 Composable。
 * @param persona 需要被展示的 Persona 数据对象。
 */
@Composable
fun PersonaCard(persona: Persona) {
    // Card 组件，提供了一个带有阴影和圆角的漂亮容器。
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Row 用于水平排列内部的组件。
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // 让头像和文字垂直居中对齐
        ) {
            // Image 组件用于显示图片。
            Image(
                // rememberAsyncImagePainter 是 Coil 库的核心功能，用于从网络异步加载图片。
                painter = rememberAsyncImagePainter(persona.avatarUrl),
                contentDescription = "${persona.name}'s avatar",
                modifier = Modifier
                    .size(64.dp) // 固定头像大小
                    .clip(CircleShape), // 将头像裁剪成圆形
                contentScale = ContentScale.Crop // 确保图片填充满圆形区域
            )

            // Spacer 用于在组件之间创建间隔。
            Spacer(modifier = Modifier.width(16.dp))

            // Column 用于垂直排列内部的组件。
            Column {
                Text(
                    text = persona.name,
                    style = MaterialTheme.typography.titleLarge // 使用主题中预设的标题样式
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = persona.personality,
                    style = MaterialTheme.typography.bodyMedium, // 使用主题中预设的正文样式
                    maxLines = 2 // 最多显示两行
                )
            }
        }
    }
}

/**
 * Preview 注解可以让我们在 Android Studio 的预览窗口中看到 Composable 的样子，
 * 而无需每次都运行应用。
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PersonaTheme {
        // 为了让预览正常工作，我们直接在这里创建一个假的 PersonaCard
        PersonaCard(persona = MockData.personas.first())
    }
}
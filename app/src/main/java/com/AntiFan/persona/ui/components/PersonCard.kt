package com.AntiFan.persona.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.AntiFan.persona.data.model.Persona

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
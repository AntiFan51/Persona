package com.AntiFan.persona.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

/**
 * 纯原生实现的打字机文本组件
 * @param text 完整的文本
 * @param animate 是否需要播放动画 (历史消息不需要播，只有新消息要播)
 */
@Composable
fun TypewriterText(
    text: String,
    animate: Boolean = true,
    color: Color = LocalContentColor.current
) {
    // 当前显示出来的字数
    var currentLength by remember { mutableIntStateOf(if (animate) 0 else text.length) }

    // 启动动画逻辑
    LaunchedEffect(text, animate) {
        if (animate) {
            // 如果是新消息，从第1个字开始慢慢蹦
            for (i in 1..text.length) {
                currentLength = i
                delay(30) // 🔥 这里调整打字速度，单位毫秒 (越小越快)
            }
        } else {
            // 如果不需要动画（比如历史记录），直接显示全部
            currentLength = text.length
        }
    }

    // 显示截取后的文本
    Text(
        text = text.take(currentLength),
        color = color,
        style = MaterialTheme.typography.bodyLarge
    )
}
package com.AntiFan.persona.ui.theme

import androidx.compose.ui.graphics.Color

// --- 全新科技风配色 ---
val PrimaryBlue = Color(0xFF2563EB)      // 主色调：深邃科技蓝
val SecondaryCyan = Color(0xFF06B6D4)    // 辅助色：赛博青
val BackgroundWhite = Color(0xFFF8FAFC)  // 背景色：冷白 (护眼)
val SurfaceWhite = Color(0xFFFFFFFF)     // 卡片色：纯白
val TextPrimary = Color(0xFF1E293B)      // 正文色：深灰蓝
val TextSecondary = Color(0xFF64748B)    // 次要文本：浅灰蓝

// --- 兼容旧代码的颜色 (为了防止删掉后报错，我们保留定义但指向新颜色) ---
val Purple80 = PrimaryBlue
val PurpleGrey80 = SecondaryCyan
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = PrimaryBlue
val PurpleGrey40 = SecondaryCyan
val Pink40 = Color(0xFF7D5260)
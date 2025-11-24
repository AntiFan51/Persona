package com.AntiFan.persona.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.AntiFan.persona.data.model.PostWithAuthor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SocialScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val feed by viewModel.feed.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ✅ 关键逻辑：每次进入页面，都自动刷新列表
    // 这样你在详情页发完贴回来，这里就能立刻看到
    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    Scaffold(
        // 这里不需要 FloatingActionButton 了，因为发帖入口在详情页
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // 浅灰背景，突出卡片
        ) {
            // 空状态提示
            if (feed.isEmpty() && !isLoading) {
                Text(
                    text = "广场空空如也，去详情页让角色发个动态吧！",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }

            // 列表显示
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feed) { item ->
                    PostItem(item)
                }
            }
        }
    }
}

/**
 * 单条动态的卡片样式
 */
@Composable
fun PostItem(item: PostWithAuthor) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. 头部：头像 + 名字 + 时间
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.authorAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(item.post.timestamp)),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. 内容文本
            Text(
                text = item.post.content,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. 底部：点赞数
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFFF5252), // 红色爱心
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.post.likeCount}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
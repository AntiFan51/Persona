package com.AntiFan.persona.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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

    // 每次进入页面自动刷新数据
    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // 浅灰背景
        ) {
            // 空状态
            if (feed.isEmpty() && !isLoading) {
                Text(
                    text = "广场空空如也，去详情页让角色发个动态吧！",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }

            // 列表
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feed) { item ->
                    PostItem(
                        item = item,
                        // ✅ 绑定 ViewModel 的交互逻辑
                        onLikeClick = { viewModel.toggleLike(item.post) },
                        onFollowClick = { viewModel.toggleFollow(item.post.authorId, item.authorIsFollowed) }
                    )
                }
            }
        }
    }
}

/**
 * 带有交互功能的动态卡片
 */
@Composable
fun PostItem(
    item: PostWithAuthor,
    onLikeClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- 1. 头部区域 ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 头像
                AsyncImage(
                    model = item.authorAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 名字与时间
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

                Spacer(modifier = Modifier.weight(1f)) // 撑开空间，把关注按钮挤到右边

                // ✅ 关注按钮
                TextButton(onClick = onFollowClick) {
                    Text(
                        text = if (item.authorIsFollowed) "已关注" else "+ 关注",
                        color = if (item.authorIsFollowed) Color.Gray else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. 内容区域 ---
            Text(
                text = item.post.content,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. 底部互动区域 ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onLikeClick() } // 点击触发点赞
                    .padding(4.dp) // 增加点击热区
            ) {
                // 图标切换：实心爱心 vs 空心爱心
                Icon(
                    imageVector = if (item.post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    // 颜色切换：红色 vs 灰色
                    tint = if (item.post.isLiked) Color(0xFFFF5252) else Color.Gray,
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
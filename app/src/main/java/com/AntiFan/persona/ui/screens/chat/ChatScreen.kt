package com.AntiFan.persona.ui.screens.chat

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PlayArrow // ✅ 使用播放图标
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.AntiFan.persona.data.model.UiMessage
import com.AntiFan.persona.ui.components.TypewriterText
import com.AntiFan.persona.util.AndroidTTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val currentPersona by viewModel.currentPersona.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val isEvolving by viewModel.isEvolving.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val context = LocalContext.current
    val listState = rememberLazyListState()

    val tts = remember { AndroidTTS(context) }

    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size + 1)
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = currentPersona?.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currentPersona?.name ?: "聊天中...")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEvolving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 16.dp), strokeWidth = 2.dp)
                    } else {
                        if (currentPersona != null) {
                            IconButton(onClick = { viewModel.triggerEvolution() }) {
                                Icon(Icons.Default.Star, contentDescription = "共生进化", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatInputArea(
                text = inputText,
                onTextChanged = viewModel::onInputChanged,
                onSendClick = viewModel::sendMessage,
                isEnabled = !isTyping && !isEvolving
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "💡 提示：点击 ▶️ 可播放语音；点击 ⭐ 可进化设定。",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp, start = 16.dp)
                    )
                }

                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    val isLast = msg == messages.last()
                    val shouldAnimate = isLast && !msg.isUser

                    MessageBubble(
                        message = msg,
                        isLastMessage = shouldAnimate,
                        onPlayAudio = {
                            currentPersona?.let { p ->
                                tts.speak(msg.content, p.personality)
                            }
                        }
                    )
                }

                if (isTyping) {
                    item {
                        Text(text = "对方正在输入...", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: UiMessage,
    isLastMessage: Boolean = false,
    onPlayAudio: () -> Unit
) {
    val isUser = message.isUser

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (!isUser) {
                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier.size(32.dp)
                ) {
                    // ✅ 修复：使用 PlayArrow 播放图标
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Surface(
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 260.dp)
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    if (isUser) {
                        Text(text = message.content, color = Color.White)
                    } else {
                        TypewriterText(
                            text = message.content,
                            animate = isLastMessage,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputArea(text: String, onTextChanged: (String) -> Unit, onSendClick: () -> Unit, isEnabled: Boolean) {
    Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = text, onValueChange = onTextChanged, modifier = Modifier.weight(1f),
                placeholder = { Text("说点什么...") }, singleLine = true, shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSendClick, enabled = isEnabled && text.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = if (isEnabled) MaterialTheme.colorScheme.primary else Color.Gray)
            }
        }
    }
}
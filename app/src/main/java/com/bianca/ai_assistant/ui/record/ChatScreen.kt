package com.bianca.ai_assistant.ui.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bianca.ai_assistant.infrastructure.room.MessageEntity
import com.bianca.ai_assistant.viewModel.chat.ChatUiEvent
import com.bianca.ai_assistant.viewModel.chat.ChatUiState
import com.bianca.ai_assistant.viewModel.chat.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@ExperimentalMaterial3Api
@Composable
fun ChatRoute(
    viewModel: ChatViewModel,
    onHistoryClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    ChatScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                ChatUiEvent.ShowHistory -> onHistoryClick()
                else -> viewModel.handleEvent(event)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onEvent: (ChatUiEvent) -> Unit,
) {

    val focus = LocalFocusManager.current
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("AI 助理") },
            navigationIcon = {
                IconButton(onClick = { onEvent(ChatUiEvent.ShowHistory) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "歷史")
                }
            }
        )

        // 自動滾動至最新訊息
        LaunchedEffect(uiState.messages) {
            if (uiState.messages.isNotEmpty()) {
                listState.animateScrollToItem(uiState.messages.lastIndex)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(uiState.messages) { msg ->
                ChatBubble(msg)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: 語音輸入 */ }) {
                Icon(Icons.Default.Mic, contentDescription = "語音")
            }
            TextField(
                value = uiState.inputText,
                onValueChange = { onEvent(ChatUiEvent.InputChanged(it)) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("請輸入...") }
            )
            IconButton(
                onClick = {
                    focus.clearFocus()
                    onEvent(ChatUiEvent.SendMessage)
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "送出")
            }
        }
    }
}

@Composable
fun ChatBubble(message: MessageEntity) {
    val isUser = message.who == "user"
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    val timeString = dateFormat.format(Date(message.time))

    Column(
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) Color(0xFFDCF8C6) else Color(0xFFEAEAEA),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp,
                color = if (isUser) Color.Black else Color.Black
            )
        }
        Text(
            text = timeString,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
    val now = System.currentTimeMillis()
    val messages = listOf(
        MessageEntity(who = "user", content = "今天午餐120元", time = now - 3600_000),
        MessageEntity(who = "ai", content = "已幫你記下 午餐 120元", time = now - 3500_000),
        MessageEntity(who = "user", content = "今天很累但是很開心", time = now - 1800_000),
        MessageEntity(who = "ai", content = "今日心情：正面，已記記事", time = now - 1700_000)
    )
    ChatScreen(
        uiState = ChatUiState(messages = messages, inputText = ""),
        onEvent = {}
    )
}

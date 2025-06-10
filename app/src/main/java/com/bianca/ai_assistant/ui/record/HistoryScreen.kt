package com.bianca.ai_assistant.ui.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bianca.ai_assistant.infrastructure.room.MessageEntity
import com.bianca.ai_assistant.model.Record
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.viewModel.chat.ChatViewModel
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryRoute(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.uiState.map { it.messages }.collectAsState(initial = emptyList())
    HistoryScreen(messages = messages, onBack = onBack)
}

@ExperimentalMaterial3Api
@Composable
fun HistoryScreen(
    messages: List<MessageEntity>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("歷史紀錄") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            var tabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("記帳", "記事")
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    messages.filter {
                        if (tabIndex == 0) it.type == "expense" else it.type == "note"
                    }
                ) { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(msg.content, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "日期：${dateFormat.format(Date(msg.time))}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun PreviewHistoryScreen() {
    val now = System.currentTimeMillis()
    val messages = listOf(
        MessageEntity(1, "user", "午餐100元", now - 3600_000, "expense"),
        MessageEntity(2, "ai", "已記錄 午餐100元", now - 3500_000, "expense"),
        MessageEntity(3, "user", "買書分享心得", now - 1800_000, "note"),
        MessageEntity(4, "ai", "已記錄 筆記", now - 1700_000, "note")
    )
    HistoryScreen(messages = messages, onBack = {})
}
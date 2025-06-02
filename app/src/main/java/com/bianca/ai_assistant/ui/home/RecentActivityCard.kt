package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.RecentActivityEntity
import com.bianca.ai_assistant.model.RecentActivityDisplay

@Composable
fun RecentActivityCard(
    activities: List<RecentActivityDisplay>,
    onClick: (RecentActivityDisplay) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("近期活動紀錄", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            activities.forEach { display ->
                val typeText = when (display.activity.type) {
                    "TASK" -> "任務"
                    "ARTICLE" -> "記事"
                    "AI" -> "AI"
                    else -> "其它"
                }
                Text(
                    text = "• [$typeText] ${display.activity.action}：${display.activity.title}" +
                            if (!display.exist) "" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (display.exist) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable() { onClick(display) }
                )
            }
        }
    }
}

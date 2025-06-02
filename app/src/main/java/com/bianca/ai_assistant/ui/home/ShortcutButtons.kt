package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShortcutButtons(
    onAddTask: () -> Unit,
    onAddNote: () -> Unit,
    onAskAI: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onAddTask, shape = CircleShape) {
            Text("新增任務")
        }
        Button(onClick = onAddNote, shape = CircleShape) {
            Text("寫記事")
        }
        Button(onClick = onAskAI, shape = CircleShape) {
            Text("問AI")
        }
    }
}
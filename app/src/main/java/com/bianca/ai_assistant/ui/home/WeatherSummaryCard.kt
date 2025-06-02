package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun WeatherSummaryCard(
    temperatureHigh: String,
    temperatureLow: String,
    weatherDescription: String,
    iconRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = weatherDescription,
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(16.dp))
            Column{
                Text("天氣：$weatherDescription", style = MaterialTheme.typography.titleMedium)
                Text("最高 $temperatureHigh / 最低 $temperatureLow", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
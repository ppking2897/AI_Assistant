package com.bianca.ai_assistant.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WeatherDetailCard(
    rainChance: Double?,       // 0.6 → 60%
    humidity: Int?,            // 例如 80
    windSpeed: Double?,        // 例如 3.5
    weatherType: String?,      // 例如 "多雲"
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("詳細天氣資訊", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(
                    icon = Icons.Default.Umbrella,
                    label = "降雨率",
                    value = rainChance?.let { "${(it * 100).toInt()}%" } ?: "--"
                )
                DetailItem(
                    icon = Icons.Default.WaterDrop,
                    label = "濕度",
                    value = humidity?.let { "$it%" } ?: "--"
                )
                DetailItem(
                    icon = Icons.Default.Air,
                    label = "風速",
                    value = windSpeed?.let { "${"%.1f".format(it)} m/s" } ?: "--"
                )
            }
            Spacer(Modifier.height(12.dp))
            weatherType?.let {
                Text(
                    text = "天氣描述：$it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 60.dp, max = 90.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherDetailCard() {
    WeatherDetailCard(
        rainChance = 0.72,
        humidity = 85,
        windSpeed = 3.6,
        weatherType = "多雲有陣雨"
    )
}

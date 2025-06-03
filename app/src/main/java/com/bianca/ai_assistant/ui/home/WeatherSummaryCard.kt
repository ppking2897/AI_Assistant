package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun WeatherSummaryCard(
    temperatureHigh: String,
    temperatureLow: String,
    weatherDescription: String,
    iconRes: Int? = null,
    iconUrl: String? = null,
    currentCityZh: String,
    cityMap: List<Pair<String, String>>,
    onCitySelected: (Pair<String, String>) -> Unit,
    isLoading: Boolean,
    onRefreshClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                // 地區選擇
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "地區：$currentCityZh",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "切換地區"
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        cityMap.forEach { (zh, en) ->
                            DropdownMenuItem(
                                text = { Text(zh) },
                                onClick = {
                                    expanded = false
                                    if (zh != currentCityZh) onCitySelected(zh to en)
                                }
                            )
                        }
                    }
                }

                // 天氣內容（loading 狀態下顯示 skeleton/圓圈）
                if (isLoading) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            // 可自訂更華麗的 shimmer/skeleton
                            Text(text = "載入中...", style = MaterialTheme.typography.titleLarge)
                            Text(text = "", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when {
                            iconUrl != null -> AsyncImage(model = iconUrl, contentDescription = null, modifier = Modifier.size(48.dp))
                            iconRes != null -> Icon(painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(48.dp))
                            else -> Spacer(modifier = Modifier.size(48.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(text = weatherDescription, style = MaterialTheme.typography.titleLarge)
                            Text(text = "高: $temperatureHigh / 低: $temperatureLow", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // 右上角 Refresh icon
            IconButton(
                onClick = onRefreshClick,
                enabled = !isLoading,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "重整天氣"
                )
            }
        }
    }
}
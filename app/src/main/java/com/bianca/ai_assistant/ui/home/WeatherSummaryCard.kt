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
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    onRefreshClick: () -> Unit,
    todayStr: String = SimpleDateFormat("M/d（E）", Locale.TAIWAN).format(Date()),
    onNavigateToWeekWeather: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .clickable {
                val enCity = cityMap.firstOrNull { it.first == currentCityZh }?.second ?: "Taipei"
                onNavigateToWeekWeather(enCity)
            }
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp , horizontal = 12.dp)
            ) {
                // Top Row: 日期、地區選單、Refresh
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = todayStr,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f).padding(start = 6.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expanded = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = currentCityZh,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 2.dp, end = 6.dp)
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
                    IconButton(
                        onClick = onRefreshClick,
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "重整天氣")
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Main Info: Weather icon + temp + desc
                if (isLoading) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = "載入中...", style = MaterialTheme.typography.titleLarge)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (iconUrl != null) {
                            AsyncImage(
                                model = iconUrl,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )
                        } else if (iconRes != null) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text(
                                text = weatherDescription,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = temperatureHigh,
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Text(
                                    text = " / $temperatureLow",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
                // 可再加一行描述或空氣品質
                // Text("空氣品質：良好", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
package com.bianca.ai_assistant.ui.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bianca.ai_assistant.model.ForecastItem
import com.bianca.ai_assistant.model.MainInfo
import com.bianca.ai_assistant.model.WeatherDesc
import com.bianca.ai_assistant.model.WindInfo
import com.bianca.ai_assistant.ui.normal.taiwanCityMap
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.utils.translateWeatherDesc
import com.bianca.ai_assistant.viewModel.weather.DaySummary
import com.bianca.ai_assistant.viewModel.weather.WeekWeatherViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun Forecast5Route(
    viewModel: WeekWeatherViewModel,
    city: String,
    onBack: () -> Unit = {},
) {
    val dailySummaries by viewModel.dailySummaries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var selectedDay by remember { mutableStateOf(0) }

    // city 改變時自動觸發查詢
    LaunchedEffect(city) {
        viewModel.loadForecast(city)
    }

    Forecast5Screen(
        dailySummaries = dailySummaries,
        isLoading = isLoading,
        selectedDay = selectedDay,
        onSelectDay = { selectedDay = it },
        cityName = taiwanCityMap.firstOrNull() { it.second == city }?.first
            ?: "臺北市" // 根據 city 找對應的中文名稱，若找不到則預設為臺北市
    )
}

@Composable
fun Forecast5Screen(
    dailySummaries: List<DaySummary>,
    isLoading: Boolean,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    cityName: String = "臺北市", // 新增這個參數，或用英文對應中文
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (dailySummaries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暫無資料")
        }
        return
    }
    var currentDay by remember { mutableStateOf(selectedDay) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        // 城市名稱
        Row(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                cityName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
        }
        // 橫向一週卡片
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            itemsIndexed(dailySummaries) { idx, day ->
                val isSelected = idx == currentDay
                Card(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clickable {
                            currentDay = idx
                            onSelectDay(idx)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val localDate = LocalDate.parse(day.date)
                        val dayOfWeek =
                            localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.TAIWAN)
                        Text(
                            "${day.date.substring(5)}\n$dayOfWeek",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                        AsyncImage(
                            model = day.iconUrl,
                            contentDescription = null,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            "${day.tempMax}°/${day.tempMin}°",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
        // 當天細節大卡片
        val today = dailySummaries.getOrNull(currentDay)
        today?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    Modifier
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "天氣詳情",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    AsyncImage(
                        model = it.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        it.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailItem(Icons.Default.Umbrella, "降雨率", "${(it.pop * 100).toInt()}%")
                        DetailItem(Icons.Default.WaterDrop, "濕度", "${it.humidity}%")
                        DetailItem(Icons.Default.Air, "風速", "${"%.1f".format(it.windSpeed)} m/s")
                    }
                    Text(
                        "最高 ${it.tempMax}°  最低 ${it.tempMin}°",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
            }

            // 三小時清單
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .navigationBarsPadding()
            ) {
                today.threeHourItems.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                item.dtTxt.substring(11, 16), // 例如 "09:00"
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.width(54.dp)
                            )
                            AsyncImage(
                                model = "https://openweathermap.org/img/wn/${item.weather.firstOrNull()?.icon}@2x.png",
                                contentDescription = null,
                                modifier = Modifier.size(34.dp).padding(horizontal = 6.dp)
                            )
                            Text(
                                "${item.main.temp.toInt()}°",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.width(44.dp)
                            )
                            Text(
                                "降雨率 ${(item.pop * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                translateWeatherDesc(item.weather.firstOrNull()?.description ?: "未知"),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 6.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// 可重用的細節小元件
@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 60.dp, max = 80.dp)
    ) {
        Icon(icon, contentDescription = null)
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// 假的三小時時段資料
fun fakeThreeHourItems(): List<ForecastItem> = listOf(
    ForecastItem(
        dt = 1717916400,
        main = MainInfo(temp = 28.0, temp_min = 27.0, temp_max = 28.0, humidity = 80),
        weather = listOf(WeatherDesc(description = "晴", icon = "01d")),
        pop = 0.1,
        wind = WindInfo(speed = 3.0),
        dtTxt = "2024-06-08 09:00:00"
    ),
    ForecastItem(
        dt = 1717927200,
        main = MainInfo(temp = 30.0, temp_min = 29.0, temp_max = 31.0, humidity = 70),
        weather = listOf(WeatherDesc(description = "多雲", icon = "03d")),
        pop = 0.2,
        wind = WindInfo(speed = 2.5),
        dtTxt = "2024-06-08 12:00:00"
    ),
    ForecastItem(
        dt = 1717938000,
        main = MainInfo(temp = 27.0, temp_min = 27.0, temp_max = 28.0, humidity = 85),
        weather = listOf(WeatherDesc(description = "小雨", icon = "10d")),
        pop = 0.6,
        wind = WindInfo(speed = 4.0),
        dtTxt = "2024-06-08 15:00:00"
    ),
)

@Composable
fun fakeDailySummaries(): List<DaySummary> = listOf(
    DaySummary(
        date = "2024-06-08",
        tempMax = 33,
        tempMin = 25,
        iconUrl = "https://openweathermap.org/img/wn/01d@2x.png",
        description = "晴時多雲",
        pop = 0.25,
        humidity = 70,
        windSpeed = 3.8,
        threeHourItems = fakeThreeHourItems()
    ),
    DaySummary(
        date = "2024-06-09",
        tempMax = 31,
        tempMin = 24,
        iconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
        description = "局部短暫陣雨",
        pop = 0.42,
        humidity = 81,
        windSpeed = 2.6,
        threeHourItems = fakeThreeHourItems()
    ),
    DaySummary(
        date = "2024-06-10",
        tempMax = 29,
        tempMin = 23,
        iconUrl = "https://openweathermap.org/img/wn/10d@2x.png",
        description = "午後雷陣雨",
        pop = 0.67,
        humidity = 78,
        windSpeed = 4.2,
        threeHourItems = fakeThreeHourItems()
    ),
)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewForecast5Screen() {
    AI_AssistantTheme {
        Forecast5Screen(
            dailySummaries = fakeDailySummaries(),
            isLoading = false,
            selectedDay = 0,
            onSelectDay = {}
        )
    }
}
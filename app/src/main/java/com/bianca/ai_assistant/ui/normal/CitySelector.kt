package com.bianca.ai_assistant.ui.normal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CitySelector(
    currentCity: String,
    cityMap: List<Pair<String, String>>,
    onCitySelected: (Pair<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(4.dp)
        ) {
            Text(text = "地區：$currentCity", style = MaterialTheme.typography.bodyLarge)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "切換地區"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cityMap.forEach { (zh, en) ->
                DropdownMenuItem(
                    text = { Text(zh) },
                    onClick = {
                        expanded = false
                        if (zh != currentCity) onCitySelected(zh to en)
                    }
                )
            }
        }
    }
}

val taiwanCityMap = listOf(
    "臺北市" to "Taipei",
    "新北市" to "New Taipei",
    "桃園市" to "Taoyuan",
    "臺中市" to "Taichung",
    "臺南市" to "Tainan",
    "高雄市" to "Kaohsiung",
    "基隆市" to "Keelung",
    "新竹市" to "Hsinchu",
    "嘉義市" to "Chiayi",
    "新竹縣" to "Hsinchu County",
    "苗栗縣" to "Miaoli",
    "彰化縣" to "Changhua",
    "南投縣" to "Nantou",
    "雲林縣" to "Yunlin",
    "嘉義縣" to "Chiayi County",
    "屏東縣" to "Pingtung",
    "宜蘭縣" to "Yilan",
    "花蓮縣" to "Hualien",
    "臺東縣" to "Taitung",
    "金門縣" to "Kinmen",
    "連江縣" to "Lienchiang",
    "澎湖縣" to "Penghu"
)


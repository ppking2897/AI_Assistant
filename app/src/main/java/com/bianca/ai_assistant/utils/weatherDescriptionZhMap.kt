package com.bianca.ai_assistant.utils

val weatherDescriptionZhMap = mapOf(
    // 晴
    "clear sky" to "晴朗",
    "few clouds" to "少雲",
    "scattered clouds" to "零星雲",
    "broken clouds" to "多雲",
    "overcast clouds" to "陰天",

    // 雨
    "light rain" to "小雨",
    "moderate rain" to "中雨",
    "heavy intensity rain" to "大雨",
    "very heavy rain" to "豪雨",
    "extreme rain" to "極端大雨",
    "freezing rain" to "凍雨",
    "shower rain" to "陣雨",
    "light intensity shower rain" to "小陣雨",
    "heavy intensity shower rain" to "大陣雨",
    "ragged shower rain" to "不規則陣雨",

    // 雷
    "thunderstorm" to "雷陣雨",
    "thunderstorm with light rain" to "輕微雷陣雨",
    "thunderstorm with rain" to "雷雨",
    "thunderstorm with heavy rain" to "強雷雨",
    "light thunderstorm" to "弱雷雨",
    "heavy thunderstorm" to "強雷雨",
    "ragged thunderstorm" to "不規則雷雨",
    "thunderstorm with drizzle" to "雷陣毛毛雨",
    "thunderstorm with heavy drizzle" to "強雷陣毛毛雨",

    // 毛毛雨
    "drizzle" to "毛毛雨",
    "light intensity drizzle" to "輕微毛毛雨",
    "heavy intensity drizzle" to "強毛毛雨",
    "drizzle rain" to "細雨",
    "light intensity drizzle rain" to "輕細雨",
    "heavy intensity drizzle rain" to "強細雨",
    "shower drizzle" to "陣性毛毛雨",

    // 雪
    "light snow" to "小雪",
    "snow" to "下雪",
    "heavy snow" to "大雪",
    "sleet" to "雨夾雪",
    "light shower sleet" to "小陣雨夾雪",
    "shower sleet" to "陣雨夾雪",
    "light rain and snow" to "小雨夾雪",
    "rain and snow" to "雨夾雪",
    "light shower snow" to "小陣雪",
    "shower snow" to "陣雪",
    "heavy shower snow" to "大陣雪",

    // 霧
    "mist" to "薄霧",
    "smoke" to "煙霧",
    "haze" to "霾",
    "sand/ dust whirls" to "沙塵旋風",
    "fog" to "霧",
    "sand" to "沙塵",
    "dust" to "塵土",
    "volcanic ash" to "火山灰",
    "squalls" to "狂風",
    "tornado" to "龍捲風"
)

fun translateWeatherDesc(en: String): String =
    weatherDescriptionZhMap[en.trim().lowercase()] ?: en
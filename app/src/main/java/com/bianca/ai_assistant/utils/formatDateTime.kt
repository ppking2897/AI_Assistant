package com.bianca.ai_assistant.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
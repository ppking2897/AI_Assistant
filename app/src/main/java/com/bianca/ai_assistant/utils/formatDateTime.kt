package com.bianca.ai_assistant.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatTimeShort(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}

fun formatDueTimeWithDateOption(millis: Long): String {
    val now = java.util.Calendar.getInstance()
    val due = java.util.Calendar.getInstance().apply { timeInMillis = millis }

    val isToday = now.get(java.util.Calendar.YEAR) == due.get(java.util.Calendar.YEAR)
            && now.get(java.util.Calendar.DAY_OF_YEAR) == due.get(java.util.Calendar.DAY_OF_YEAR)

    return if (isToday) {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        sdf.format(due.time)
    } else {
        val sdf = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault())
        sdf.format(due.time)
    }
}
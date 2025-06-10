package com.bianca.ai_assistant.model

data class Message(val who: String, val content: String, val time: Long)
data class Record(val type: String, val content: String, val date: String, val amount: Int? = null)
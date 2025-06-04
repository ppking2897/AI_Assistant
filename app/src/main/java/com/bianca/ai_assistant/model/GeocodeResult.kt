package com.bianca.ai_assistant.model

data class GeocodeResult(
    val name: String,
    val local_names: Map<String, String>?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)
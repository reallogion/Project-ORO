package com.oro.local.data.model

enum class RiskLevel { OK, WARNING, CRITICAL }

data class RiskResult(
    val level: RiskLevel,
    val learning: Boolean,
    val explanation: String,
    val deviatingVitals: List<String>
)
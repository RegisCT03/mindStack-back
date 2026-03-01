package com.MindStack.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class GameSession(
    val id: Int = 0,
    val idDailyCheckin: Int,
    val idGame: Int,
    val startTime: String? = null,
    val endTime: String? = null,
    val scoreValue: Double? = null,
    val battery: Int? = null,
    val metadata: String? = null
)
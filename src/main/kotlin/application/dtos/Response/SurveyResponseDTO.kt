package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SurveyResponseDto(
    val surveyId: Int,
    val streakMilestone: Int,
    val answers: JsonObject,
    val avgSleepLast10: Double,
    val avgBatteryLast10: Double,
    val answeredAt: String,
    val message: String
)
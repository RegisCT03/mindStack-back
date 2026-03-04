package com.MindStack.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class StreakInfo(
    val id: Int = 0,
    val userId: Int,
    val startDate: String,
    val endDate: String? = null,
    val daysCount: Int = 1
)
package com.MindStack.domain.interfaces.repositories
import com.MindStack.domain.models.SurveyResponse

interface ISurveyRepository {
    suspend fun save(
        userId: Int,
        milestone: Int,
        answersJson: String,
        avgSleepLast10: Double,
        avgBatteryLast10: Double
    ): SurveyResponse

    suspend fun findByUser(userId: Int): List<SurveyResponse>
    suspend fun alreadyAnswered(userId: Int, milestone: Int): Boolean
}
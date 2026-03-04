package com.MindStack.application.services

import ISurveyRepository
import SurveyRequest
import com.MindStack.application.dtos.Response.SurveyResponseDto
import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class SurveyService(
    private val surveyRepo: ISurveyRepository,
    private val checkinRepo: IDailyCheckinRepository
) {

    suspend fun submit(userId: Int, req: SurveyRequest): SurveyResponseDto {
        if (surveyRepo.alreadyAnswered(userId, req.streakMilestone)) {
            throw IllegalArgumentException(
                "Ya respondiste la encuesta del día ${req.streakMilestone}."
            )
        }

        val last10 = checkinRepo.findLastN(userId, 10)
        val avgSleep = last10.mapNotNull { it.hoursSleep }
            .takeIf { it.isNotEmpty() }?.average() ?: 0.0
        val avgBattery    = last10.mapNotNull { it.batteryCog?.toDouble() }
            .takeIf { it.isNotEmpty() }?.average() ?: 0.0

        val answersJson   = Json.encodeToString(req.answers)

        val saved = surveyRepo.save(
            userId           = userId,
            milestone        = req.streakMilestone,
            answersJson      = answersJson,
            avgSleepLast10   = avgSleep,
            avgBatteryLast10 = avgBattery
        )

        return SurveyResponseDto(
            surveyId         = saved.id,
            streakMilestone  = saved.streakMilestone,
            answers          = req.answers,
            avgSleepLast10   = avgSleep,
            avgBatteryLast10 = avgBattery,
            answeredAt       = saved.answeredAt,
            message          = "¡Gracias! Tu respuesta del día ${req.streakMilestone} fue guardada."
        )
    }

    suspend fun getHistory(userId: Int): List<SurveyResponseDto> {
        return surveyRepo.findByUser(userId).map { s ->
            val answers = Json.parseToJsonElement(s.answers) as JsonObject
            SurveyResponseDto(
                surveyId         = s.id,
                streakMilestone  = s.streakMilestone,
                answers          = answers,
                avgSleepLast10   = s.avgSleepLast10   ?: 0.0,
                avgBatteryLast10 = s.avgBatteryLast10 ?: 0.0,
                answeredAt       = s.answeredAt,
                message          = ""
            )
        }
    }
}
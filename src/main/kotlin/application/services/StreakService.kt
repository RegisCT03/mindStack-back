package com.MindStack.application.services

import com.MindStack.application.dtos.Response.StreakResponse
import com.MindStack.domain.interfaces.repositories.IStreakRepository
import com.MindStack.domain.models.StreakInfo
import java.time.LocalDate

class StreakService(private val streakRepo: IStreakRepository) {

    suspend fun refresh(userId: Int): StreakResponse {
        val today  = LocalDate.now()
        val active = streakRepo.findActive(userId)

        if (active == null) {
            val new = streakRepo.createNew(userId)
            return buildResponse(new, surveyPending = false)
        }

        val lastDay = LocalDate.parse(active.startDate)
            .plusDays((active.daysCount - 1).toLong())

        return when {
            lastDay == today -> {
                // Ya registró hoy — no tocar la racha
                buildResponse(active, surveyPending = false)
            }
            lastDay.plusDays(1) == today -> {
                // Día consecutivo: incrementar
                val updated = streakRepo.increment(active.id)
                // Verificar si llegó a un punto de encuesta (múltiplo de 10)
                val isHito = updated.daysCount % 10 == 0
                buildResponse(updated, surveyPending = isHito)
            }
            else -> {
                // Se saltó un día: reiniciar racha
                streakRepo.close(active.id)
                val new = streakRepo.createNew(userId)
                buildResponse(new, surveyPending = false)
            }
        }
    }

    suspend fun getStreakInfo(userId: Int): StreakResponse {
        val active  = streakRepo.findActive(userId)
        val current = active?.daysCount ?: 0
        val longest = streakRepo.longestStreak(userId)
        val total   = streakRepo.totalDays(userId)
        return StreakResponse(
            currentStreak   = current,
            longestStreak   = longest,
            totalDays       = total,
            goalDays        = 20,
            progressPercent = (current / 20.0 * 100.0).coerceAtMost(100.0),
            isGoalAchieved  = total >= 20,
            surveyPending   = false,
            surveyMilestone = null
        )
    }

    private suspend fun buildResponse(
        streak: StreakInfo,
        surveyPending: Boolean
    ): StreakResponse {
        val longest = streakRepo.longestStreak(streak.userId)
        val total   = streakRepo.totalDays(streak.userId)
        val milestone = if (surveyPending) streak.daysCount else null
        return StreakResponse(
            currentStreak   = streak.daysCount,
            longestStreak   = longest,
            totalDays       = total,
            goalDays        = 20,
            progressPercent = (streak.daysCount / 20.0 * 100.0).coerceAtMost(100.0),
            isGoalAchieved  = total >= 20,
            surveyPending   = surveyPending,
            surveyMilestone = milestone
        )
    }
}

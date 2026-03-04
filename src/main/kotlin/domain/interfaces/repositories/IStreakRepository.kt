package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.StreakInfo

interface IStreakRepository {
    /** Racha activa (endDate IS NULL). Null si no existe. */
    suspend fun findActive(userId: Int): StreakInfo?

    /** Crea racha nueva con daysCount = 1 y startDate = hoy */
    suspend fun createNew(userId: Int): StreakInfo

    /** Suma 1 al daysCount de la racha activa */
    suspend fun increment(streakId: Int): StreakInfo

    /** Cierra la racha (endDate = hoy) */
    suspend fun close(streakId: Int)

    /** Racha más larga histórica del usuario */
    suspend fun longestStreak(userId: Int): Int

    /** Total de días con check-in en toda la historia */
    suspend fun totalDays(userId: Int): Int
}
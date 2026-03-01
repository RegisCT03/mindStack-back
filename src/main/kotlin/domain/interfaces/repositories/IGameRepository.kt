package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.GameSession
import com.MindStack.domain.models.Message

interface IGameSessionRepository {
    suspend fun create(
        idDailyCheckin: Int, idGame: Int,
        scoreValue: Double, battery: Int, metadata: String
    ): GameSession
    suspend fun findByCheckin(checkinId: Int): List<GameSession>
}
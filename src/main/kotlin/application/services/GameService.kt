package com.MindStack.application.services

import com.MindStack.application.dtos.PersonalizedMessage
import com.MindStack.application.dtos.Request.MemoryGameRequest
import com.MindStack.application.dtos.Request.NeuroReflexRequest
import com.MindStack.application.dtos.Response.CombinedBatteryResponse
import com.MindStack.application.dtos.Response.MemoryGameResponse
import com.MindStack.application.dtos.Response.NeuroReflexResponse
import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import com.MindStack.domain.interfaces.repositories.IGameSessionRepository
import com.MindStack.domain.interfaces.repositories.IMessageRepository
import com.MindStack.domain.interfaces.repositories.IUserRepository
import com.MindStack.domain.interfaces.services.IGameService

private const val JUEGO_NEURO_REFLEX = 1
private const val JUEGO_MEMORY       = 2

class GameService(
    private val gameSessionRepo: IGameSessionRepository,
    private val checkinRepo: IDailyCheckinRepository,
    private val messageRepo: IMessageRepository,
    private val userRepo: IUserRepository
) : IGameService {

    override suspend fun submitNeuroReflex(userId: Int, req: NeuroReflexRequest): NeuroReflexResponse {
        val checkin = checkinRepo.findById(req.idDailyCheckin)
            ?: throw IllegalArgumentException("Check-in no encontrado.")
        if (checkin.idUser != userId)
            throw IllegalArgumentException("No autorizado.")

        if (checkin.sleepEnd == null)
            throw IllegalArgumentException("Debes registrar tu despertar antes de jugar.")

        val averageMs = (req.reactionTime1Ms + req.reactionTime2Ms + req.reactionTime3Ms) / 3.0
        val battery   = CognitiveBatteryEngine.neuroReflexBattery(averageMs)
        val result    = CognitiveBatteryEngine.evaluate(battery)

        val metadata = """{"game":"taptap","time1Ms":${req.reactionTime1Ms},"time2Ms":${req.reactionTime2Ms},"time3Ms":${req.reactionTime3Ms},"averageMs":$averageMs}"""

        val session = gameSessionRepo.create(
            idDailyCheckin = req.idDailyCheckin,
            idGame         = JUEGO_NEURO_REFLEX,
            scoreValue     = averageMs,
            battery        = battery,
            metadata       = metadata
        )

        refreshCombinedBattery(req.idDailyCheckin)

        messageRepo.create(
            idDailyCheckin = null,
            idGameSession  = session.id,
            message        = result.recommendation
        )

        return NeuroReflexResponse(
            sessionId = session.id,
            averageMs = averageMs,
            battery = battery,
            label = result.label,
            recommendation = result.recommendation
        )
    }

    override suspend fun submitMemoryGame(userId: Int, req: MemoryGameRequest): MemoryGameResponse {
        if (req.totalRequired <= 0)
            throw IllegalArgumentException("totalRequired debe ser mayor a 0.")
        if (req.correctHits < 0)
            throw IllegalArgumentException("correctHits no puede ser negativo.")
        if (req.correctHits > req.totalRequired)
            throw IllegalArgumentException("correctHits no puede superar totalRequired.")

        val checkin = checkinRepo.findById(req.idDailyCheckin)
            ?: throw IllegalArgumentException("Check-in no encontrado.")
        if (checkin.idUser != userId)
            throw IllegalArgumentException("No autorizado.")

        if (checkin.sleepEnd == null)
            throw IllegalArgumentException("Debes registrar tu despertar antes de jugar.")

        val accuracy = (req.correctHits.toDouble() / req.totalRequired.toDouble()) * 100.0
        val battery  = CognitiveBatteryEngine.memoryBattery(accuracy)
        val result   = CognitiveBatteryEngine.evaluate(battery)

        val metadata = """{"game":"memorama","correctHits":${req.correctHits},"totalRequired":${req.totalRequired},"accuracyPercent":$accuracy}"""

        val session = gameSessionRepo.create(
            idDailyCheckin = req.idDailyCheckin,
            idGame         = JUEGO_MEMORY,
            scoreValue     = accuracy,
            battery        = battery,
            metadata       = metadata
        )

        refreshCombinedBattery(req.idDailyCheckin)

        messageRepo.create(
            idDailyCheckin = null,
            idGameSession  = session.id,
            message        = result.recommendation
        )

        return MemoryGameResponse(
            sessionId = session.id,
            accuracyPercent = accuracy,
            battery = battery,
            label = result.label,
            recommendation = result.recommendation
        )
    }

    override suspend fun getCombinedBattery(userId: Int, checkinId: Int): CombinedBatteryResponse {
        val checkin = checkinRepo.findById(checkinId)
            ?: throw IllegalArgumentException("Check-in no encontrado.")
        if (checkin.idUser != userId)
            throw IllegalArgumentException("No autorizado.")

        val sessions = gameSessionRepo.findByCheckin(checkinId)
        val batteryA = sessions.firstOrNull { it.idGame == JUEGO_NEURO_REFLEX }?.battery
        val batteryB = sessions.firstOrNull { it.idGame == JUEGO_MEMORY }?.battery
        val combined = CognitiveBatteryEngine.combinedBattery(batteryA, batteryB)
        val cognitive = CognitiveBatteryEngine.evaluate(combined)

        val semaphoreColor = when (checkin.idSemaphore) {
            1    -> "GREEN"
            2    -> "YELLOW"
            3    -> "RED"
            else -> "UNKNOWN"
        }

        val globalRec = if (semaphoreColor == "UNKNOWN")
            "⚪ Aún no has registrado tu despertar. Completa el check-in de sueño primero."
        else
            CognitiveBatteryEngine.globalRecommendation(semaphoreColor, combined)

        val user      = userRepo.findById(userId)
        val msgResult = MessageEngine.getMessage(
            idRol          = user?.idRol,
            semaphoreColor = if (semaphoreColor == "UNKNOWN") "RED" else semaphoreColor,
            batteryLevel   = combined
        )

        return CombinedBatteryResponse(
            finalBattery = combined,
            fatiga = (100 - combined).coerceAtLeast(0),
            semaphoreColor = semaphoreColor,
            cognitiveSemaphore = cognitive.label,
            globalRecommendation = globalRec,
            personalizedMessage = PersonalizedMessage(
                prefix = msgResult.prefix,
                body = msgResult.body,
                full = msgResult.full,
                batteryRange = msgResult.batteryRange
            )
        )
    }

    private suspend fun refreshCombinedBattery(checkinId: Int) {
        val sessions = gameSessionRepo.findByCheckin(checkinId)
        val batteryA = sessions.firstOrNull { it.idGame == JUEGO_NEURO_REFLEX }?.battery
        val batteryB = sessions.firstOrNull { it.idGame == JUEGO_MEMORY }?.battery
        checkinRepo.updateBattery(
            checkinId,
            CognitiveBatteryEngine.combinedBattery(batteryA, batteryB)
        )
    }
}
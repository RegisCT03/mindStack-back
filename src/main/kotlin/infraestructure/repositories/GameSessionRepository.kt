package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IGameSessionRepository
import com.MindStack.domain.models.GameSession
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.GameSessionsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant

class GameSessionRepository : IGameSessionRepository {

    override suspend fun create(
        idDailyCheckin: Int, idGame: Int,
        scoreValue: Double, battery: Int, metadata: String
    ): GameSession = dbQuery {
        val now = Instant.now()
        val insertedId = GameSessionsTable.insert {
            it[GameSessionsTable.idDailyCheckin] = idDailyCheckin
            it[GameSessionsTable.idGame]        = idGame
            it[GameSessionsTable.startTime]      = now
            it[GameSessionsTable.endTime]        = now
            it[GameSessionsTable.scoreValue]     = scoreValue
            it[GameSessionsTable.battery]        = battery
            it[GameSessionsTable.metadata]       = metadata
        } get GameSessionsTable.id

        GameSession(
            id             = insertedId,
            idDailyCheckin = idDailyCheckin,
            idGame        = idGame,
            startTime      = now.toString(),
            endTime        = now.toString(),
            scoreValue     = scoreValue,
            battery        = battery,
            metadata       = metadata
        )
    }

    override suspend fun findByCheckin(checkinId: Int): List<GameSession> = dbQuery {
        GameSessionsTable.selectAll().where { GameSessionsTable.idDailyCheckin eq checkinId }
            .map { row ->
                GameSession(
                    id             = row[GameSessionsTable.id],
                    idDailyCheckin = row[GameSessionsTable.idDailyCheckin],
                    idGame        = row[GameSessionsTable.idGame] ?: 0,
                    startTime      = row[GameSessionsTable.startTime].toString(),
                    endTime        = row[GameSessionsTable.endTime].toString(),
                    scoreValue     = row[GameSessionsTable.scoreValue],
                    battery        = row[GameSessionsTable.battery],
                    metadata       = row[GameSessionsTable.metadata]
                )
            }
    }
}
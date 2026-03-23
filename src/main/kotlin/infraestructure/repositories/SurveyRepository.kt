package com.MindStack.infraestructure.repositories

import ISurveyRepository
import SurveyResponse
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.SurveyResponseTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant

class SurveyRepository : ISurveyRepository {

    override suspend fun save(
        userId: Int,
        milestone: Int,
        answersJson: String,
        avgSleepLast10: Double,
        avgBatteryLast10: Double
    ): SurveyResponse = dbQuery {
        val now = Instant.now()
        val id = SurveyResponseTable.insert {
            it[SurveyResponseTable.idUser]           = userId
            it[SurveyResponseTable.streakMilestone]  = milestone
            it[SurveyResponseTable.answers]          = answersJson
            it[SurveyResponseTable.avgSleepLast10]   = avgSleepLast10
            it[SurveyResponseTable.avgBatteryLast10] = avgBatteryLast10
            it[SurveyResponseTable.answeredAt]       = now
        } get SurveyResponseTable.id

        SurveyResponse(
            id               = id,
            idUser           = userId,
            streakMilestone  = milestone,
            answers          = answersJson,
            avgSleepLast10   = avgSleepLast10,
            avgBatteryLast10 = avgBatteryLast10,
            answeredAt       = now.toString()
        )
    }

    override suspend fun findByUser(userId: Int): List<SurveyResponse> = dbQuery {
        SurveyResponseTable.selectAll().where { SurveyResponseTable.idUser eq userId }
            .orderBy(SurveyResponseTable.answeredAt, SortOrder.DESC)
            .map { toModel(it) }
    }

    override suspend fun alreadyAnswered(userId: Int, milestone: Int): Boolean = dbQuery {
        SurveyResponseTable.selectAll().where {
            (SurveyResponseTable.idUser eq userId) and
                    (SurveyResponseTable.streakMilestone eq milestone)
        }.count() > 0
    }

    private fun toModel(row: ResultRow) = SurveyResponse(
        id               = row[SurveyResponseTable.id],
        idUser           = row[SurveyResponseTable.idUser],
        streakMilestone  = row[SurveyResponseTable.streakMilestone],
        answers          = row[SurveyResponseTable.answers],
        avgSleepLast10   = row[SurveyResponseTable.avgSleepLast10],
        avgBatteryLast10 = row[SurveyResponseTable.avgBatteryLast10],
        answeredAt       = row[SurveyResponseTable.answeredAt].toString()
    )
}
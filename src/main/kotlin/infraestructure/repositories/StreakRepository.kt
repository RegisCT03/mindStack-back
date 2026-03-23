package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IStreakRepository
import com.MindStack.domain.models.StreakInfo
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.StreaksHistoryTable
import com.MindStack.infraestructure.database.entities.StreaksHistoryTable.endDate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

class StreakRepository : IStreakRepository {

    override suspend fun findActive(userId: Int): StreakInfo? = dbQuery {
        StreaksHistoryTable.selectAll().where {
            (StreaksHistoryTable.userId eq userId) and StreaksHistoryTable.endDate.isNull()
        }
            .orderBy(StreaksHistoryTable.startDate, SortOrder.DESC)
            .map { toModel(it) }
            .firstOrNull()
    }

    override suspend fun createNew(userId: Int): StreakInfo = dbQuery {
        val today = LocalDate.now()
        val id = StreaksHistoryTable.insert {
            it[StreaksHistoryTable.userId]    = userId
            it[StreaksHistoryTable.startDate] = today
            it[StreaksHistoryTable.daysCount] = 1
        } get StreaksHistoryTable.id
        StreakInfo(id = id, userId = userId, startDate = today.toString(), daysCount = 1)
    }

    override suspend fun increment(streakId: Int): StreakInfo = dbQuery {
        val currentRecord = StreaksHistoryTable.selectAll()
            .where { StreaksHistoryTable.id eq streakId }
            .single()
        val currentDays = currentRecord[StreaksHistoryTable.daysCount]
        StreaksHistoryTable.update({ StreaksHistoryTable.id eq streakId }) {
            it[StreaksHistoryTable.daysCount] = currentDays + 1
        }
        StreaksHistoryTable.selectAll()
            .where { StreaksHistoryTable.id eq streakId }
            .map { toModel(it) }
            .first()
    }

    override suspend fun close(streakId: Int): Unit = dbQuery {
        StreaksHistoryTable.update({ StreaksHistoryTable.id eq streakId }) {
            it[endDate] = LocalDate.now()
        }
    }

    override suspend fun longestStreak(userId: Int): Int = dbQuery {
        StreaksHistoryTable.selectAll().where { StreaksHistoryTable.userId eq userId }
            .maxOfOrNull { it[StreaksHistoryTable.daysCount] } ?: 0
    }

    override suspend fun totalDays(userId: Int): Int = dbQuery {
        StreaksHistoryTable.selectAll().where { StreaksHistoryTable.userId eq userId }
            .sumOf { it[StreaksHistoryTable.daysCount] }
    }

    private fun toModel(row: ResultRow) = StreakInfo(
        id        = row[StreaksHistoryTable.id],
        userId    = row[StreaksHistoryTable.userId] ?: 0,
        startDate = row[StreaksHistoryTable.startDate].toString(),
        endDate   = row[StreaksHistoryTable.endDate]?.toString(),
        daysCount = row[StreaksHistoryTable.daysCount]
    )
}
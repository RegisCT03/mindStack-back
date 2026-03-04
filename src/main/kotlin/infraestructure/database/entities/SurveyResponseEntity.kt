package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SurveyResponseTable : Table("survey_response") {
    val id                = integer("id").autoIncrement()
    val idUser            = integer("id_user").references(UsersTable.id)
    val streakMilestone   = integer("streak_milestone")
    val answers           = jsonb("answers")
    val avgSleepLast10    = double("avg_sleep_last10").nullable()
    val avgBatteryLast10  = double("avg_battery_last10").nullable()
    val answeredAt        = timestamp("answered_at")
    override val primaryKey = PrimaryKey(id)
}

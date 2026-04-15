package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object OtpTable : Table("otp_codes") {
    val id        = integer("id").autoIncrement()
    val idUser    = integer("id_user").references(UsersTable.id)
    val code      = varchar("code", 6)
    val expiresAt = timestamp("expires_at")
    val used      = bool("used").default(false)
    override val primaryKey = PrimaryKey(id)
}

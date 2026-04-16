package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IOtpRepository
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.OtpCodeTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class OtpRepository : IOtpRepository {

    override suspend fun save(userId: Int, code: String, expiresAt: Instant): Unit = dbQuery {
        OtpCodeTable.update({
            (OtpCodeTable.idUser eq userId) and (OtpCodeTable.used eq false)
        }) {
            it[OtpCodeTable.used] = true
        }

        OtpCodeTable.insert {
            it[OtpCodeTable.idUser]    = userId
            it[OtpCodeTable.code]      = code
            it[OtpCodeTable.expiresAt] = expiresAt
            it[OtpCodeTable.used]      = false
            it[OtpCodeTable.createdAt] = Instant.now()
        }
    }

    override suspend fun validate(userId: Int, code: String): Boolean = dbQuery {
        val now = Instant.now()
        OtpCodeTable.selectAll().where {
            (OtpCodeTable.idUser    eq userId) and
                    (OtpCodeTable.code      eq code)   and
                    (OtpCodeTable.used      eq false)  and
                    (OtpCodeTable.expiresAt greater now)
        }.count() > 0
    }

    override suspend fun markUsed(userId: Int, code: String): Unit = dbQuery {
        OtpCodeTable.update({
            (OtpCodeTable.idUser eq userId) and (OtpCodeTable.code eq code)
        }) {
            it[OtpCodeTable.used] = true
        }
    }
}
package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IOtpRepository
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.OtpTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class OtpRepository : IOtpRepository {

    /** Invalida todos los OTP previos del usuario e inserta uno nuevo */
    override suspend fun save(userId: Int, code: String, expiresAt: Instant): Unit = dbQuery {
        // Marcar todos los anteriores como usados para este usuario
        OtpTable.update({
            (OtpTable.idUser eq userId) and (OtpTable.used eq false)
        }) {
            it[OtpTable.used] = true
        }

        OtpTable.insert {
            it[OtpTable.idUser]    = userId
            it[OtpTable.code]      = code
            it[OtpTable.expiresAt] = expiresAt
            it[OtpTable.used]      = false
        }
    }

    /** Retorna true si existe un código válido: coincide, no expirado, no usado */
    override suspend fun validate(userId: Int, code: String): Boolean = dbQuery {
        val now = Instant.now()
        OtpTable.selectAll().where {
            (OtpTable.idUser    eq userId)  and
                    (OtpTable.code      eq code)    and
                    (OtpTable.used      eq false)   and
                    (OtpTable.expiresAt greater now)
        }.count() > 0
    }

    /** Marca el código como usado una vez verificado correctamente */
    override suspend fun markUsed(userId: Int, code: String): Unit = dbQuery {
        OtpTable.update({
            (OtpTable.idUser eq userId) and (OtpTable.code eq code)
        }) {
            it[OtpTable.used] = true
        }
    }
}

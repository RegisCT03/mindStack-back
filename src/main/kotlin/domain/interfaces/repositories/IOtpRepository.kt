package com.MindStack.domain.interfaces.repositories

interface IOtpRepository {
    /** Guarda un nuevo código OTP para el usuario. Invalida los anteriores. */
    suspend fun save(userId: Int, code: String, expiresAt: java.time.Instant)

    /** Devuelve el userId si el código es válido, no expirado y no usado. Null si falla. */
    suspend fun validate(userId: Int, code: String): Boolean

    /** Marca el código como usado */
    suspend fun markUsed(userId: Int, code: String)
}

package com.MindStack.domain.interfaces.repositories

import java.time.Instant

interface IOtpRepository {
    suspend fun save(userId: Int, code: String, expiresAt: Instant)
    suspend fun validate(userId: Int, code: String): Boolean
    suspend fun markUsed(userId: Int, code: String)
}
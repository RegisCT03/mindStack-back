package com.MindStack.domain.interfaces.services

import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
import com.MindStack.application.dtos.Request.VerifyOtpRequest
import com.MindStack.application.dtos.Response.AuthResponse
import com.MindStack.application.dtos.Response.PreAuthResponse

interface IAuthService {
    suspend fun register(req: RegisterRequest): AuthResponse
    suspend fun login(req: LoginRequest): PreAuthResponse
    suspend fun verifyOtp(req: VerifyOtpRequest): AuthResponse
}
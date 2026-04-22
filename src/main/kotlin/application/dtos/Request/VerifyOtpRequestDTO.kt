package com.MindStack.application.dtos.Request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRequest(
    val preAuthToken: String,
    val code: String
)
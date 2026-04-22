package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class PreAuthResponse(
    val preAuthToken: String,
    val message: String = "Código de verificación enviado a tu correo electrónico."
)
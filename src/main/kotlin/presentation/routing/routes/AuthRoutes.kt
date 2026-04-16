package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
import com.MindStack.application.dtos.Request.VerifyOtpRequest
import com.MindStack.domain.interfaces.services.IAuthService
import com.MindStack.presentation.plugins.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: IAuthService) {
    route("/auth") {

        // POST /api/v1/auth/register
        post("/register") {
            val req = call.receive<RegisterRequest>()
            val passwordError = validatePassword(req.password)
            if (passwordError != null) {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse(passwordError))
            }
            val res = authService.register(req)
            call.respond(HttpStatusCode.Created, res)
        }

        // POST /api/v1/auth/login
        post("/login") {
            val req = call.receive<LoginRequest>()
            val res = authService.login(req)
            call.respond(HttpStatusCode.OK, res)
        }

        // POST /api/v1/auth/verify-otp
        post("/verify-otp") {
            val req = call.receive<VerifyOtpRequest>()
            val res = authService.verifyOtp(req)
            call.respond(HttpStatusCode.OK, res)
        }
    }
}

fun validatePassword(password: String): String? {
    val errors = mutableListOf<String>()
    if (password.length < 8)                errors.add("mínimo 8 caracteres")
    if (!password.any { it.isUpperCase() }) errors.add("al menos una letra mayúscula")
    if (!password.any { it.isLowerCase() }) errors.add("al menos una letra minúscula")
    if (!password.any { it.isDigit() })     errors.add("al menos un número")
    if (!password.any { it in "!@#\$%^&*()_+-=[]{}|;':\",./<>?" })
        errors.add("al menos un carácter especial (!@#\$%^&*...)")
    if (password.length > 128)              errors.add("máximo 128 caracteres")
    return if (errors.isEmpty()) null
    else "La contraseña debe tener: ${errors.joinToString(", ")}."
}
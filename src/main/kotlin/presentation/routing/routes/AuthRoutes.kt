package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
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

            if (req.password.length < 8) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("La contraseña debe tener mínimo 8 caracteres.")
                )
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
    }
}
package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.Request.SleepStartRequest
import com.MindStack.application.dtos.Response.SleepEndRequest
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import com.MindStack.presentation.plugins.ErrorResponse
import com.MindStack.presentation.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.sleepRoutes(checkinService: IDailyCheckinService) {
    authenticate("auth-jwt") {
        route("/sleep") {

            // POST /api/v1/sleep/start
            post("/start") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<SleepStartRequest>()

                val existing = checkinService.findOpenToday(userId)
                if (existing != null) {
                    return@post call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse("Ya tienes un registro de sueño iniciado hoy.")
                    )
                }

                val res = checkinService.startSleep(userId, req)
                call.respond(HttpStatusCode.Created, res)
            }

            // PUT /api/v1/sleep/{checkinId}/end
            put("/{checkinId}/end") {
                val userId    = call.principal<JWTPrincipal>()!!.userId()
                val checkinId = call.parameters["checkinId"]?.toIntOrNull()
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("checkinId inválido")
                    )
                val req = call.receive<SleepEndRequest>()

                if (req.moodScore !in 1..5)
                    return@put call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("moodScore debe ser un valor entre 1 y 5")
                    )

                val res = checkinService.endSleep(userId, checkinId, req)
                call.respond(HttpStatusCode.OK, res)
            }
        }
    }
}
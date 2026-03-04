package com.MindStack.presentation.routing.routes

import SurveyRequest
import com.MindStack.application.services.SurveyService
import com.MindStack.presentation.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.surveyRoutes(surveyService: SurveyService) {
    authenticate("auth-jwt") {
        route("/survey") {

            // POST /api/v1/survey
            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<SurveyRequest>()

                if (req.streakMilestone <= 0 || req.streakMilestone % 10 != 0)
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "streakMilestone debe ser un múltiplo de 10 (10, 20, 30...)")
                    )

                val res = surveyService.submit(userId, req)
                call.respond(HttpStatusCode.Created, res)
            }

            // GET /api/v1/survey/history
            get("/history") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(HttpStatusCode.OK, surveyService.getHistory(userId))
            }
        }
    }
}

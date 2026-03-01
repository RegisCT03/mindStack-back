package com.MindStack.presentation.routing

import com.MindStack.presentation.di.DependenciesDeclaration
import com.MindStack.presentation.routing.routes.authRoutes
import com.MindStack.presentation.routing.routes.checkinRoutes
import com.MindStack.presentation.routing.routes.gameRoutes
import com.MindStack.presentation.routing.routes.streakRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting(deps: DependenciesDeclaration) {
    routing {
        get("/") {
            call.respondText("MindStack API — OK")
        }
        route("/api/v1") {
            authRoutes(deps.authService)
            checkinRoutes(deps.checkinService)
            gameRoutes(deps.gameService)
        }
        streakRoutes(deps.streakService)
    }
}
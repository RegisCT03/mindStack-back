package com.MindStack.presentation.di

import com.MindStack.application.services.AuthService
import com.MindStack.application.services.DailyCheckinService
import com.MindStack.application.services.GameService
import com.MindStack.application.services.StreakService
import com.MindStack.application.services.SurveyService
import com.MindStack.domain.interfaces.services.IAuthService
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import com.MindStack.domain.interfaces.services.IGameService
import com.MindStack.infraestructure.repositories.DailyCheckinRepository
import com.MindStack.infraestructure.repositories.GameSessionRepository
import com.MindStack.infraestructure.repositories.MessageRepository
import com.MindStack.infraestructure.repositories.StreakRepository
import com.MindStack.infraestructure.repositories.SurveyRepository
import com.MindStack.infraestructure.repositories.UserRepository

class DependenciesDeclaration {

    private val userRepo = UserRepository()
    private val checkinRepo = DailyCheckinRepository()
    private val messageRepo = MessageRepository()
    private val gameSessionRepo = GameSessionRepository()
    private val streakRepo = StreakRepository()
    private val surveyRepo = SurveyRepository()

    val authService: IAuthService = AuthService(userRepo = userRepo)

    val streakService = StreakService(streakRepo = streakRepo)

    val checkinService: IDailyCheckinService = DailyCheckinService(
        checkinRepo = checkinRepo,
        userRepo = userRepo,
        messageRepo = messageRepo,
        streakService = streakService
    )

    val gameService: IGameService = GameService(
        gameSessionRepo = gameSessionRepo,
        checkinRepo = checkinRepo,
        messageRepo = messageRepo,
        userRepo = userRepo
    )

    val surveyService = SurveyService(
        surveyRepo = surveyRepo,
        checkinRepo = checkinRepo
    )
}
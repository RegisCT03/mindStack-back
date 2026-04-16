package com.MindStack.application.services

import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
import com.MindStack.application.dtos.Request.VerifyOtpRequest
import com.MindStack.application.dtos.Response.AuthResponse
import com.MindStack.application.dtos.Response.PreAuthResponse
import com.MindStack.domain.interfaces.repositories.IOtpRepository
import com.MindStack.domain.interfaces.repositories.IUserRepository
import com.MindStack.domain.interfaces.services.IAuthService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import java.time.Instant
import java.util.Date
import java.util.UUID

class AuthService(
    private val userRepo: IUserRepository,
    private val otpRepo: IOtpRepository,
    private val emailService: IEmailService,
    private val jwtSecret: String   = System.getenv("JWT_SECRET")   ?: "mindstack_secret_dev",
    private val jwtIssuer: String   = System.getenv("JWT_ISSUER")   ?: "mindstack",
    private val jwtAudience: String = System.getenv("JWT_AUDIENCE") ?: "mindstack_users",
    private val jwtExpiration: Long  = 86_400_000L,
    private val preAuthExpiry: Long  = 600_000L,
    private val otpExpirySeconds: Long = 600L
) : IAuthService {

    override suspend fun register(req: RegisterRequest): AuthResponse {
        userRepo.findByEmail(req.email)?.let {
            throw IllegalArgumentException("El email ya está registrado.")
        }
        val hashed = BCrypt.hashpw(req.password, BCrypt.gensalt())
        val user = userRepo.create(
            name            = req.name,
            lastName        = req.lastName,
            email           = req.email,
            hashedPassword  = hashed,
            dateOfBirth     = req.dateOfBirth,
            gender          = req.gender,
            idealSleepHours = req.idealSleepHours
        )
        return AuthResponse(
            token  = buildSessionJwt(user.id, user.email),
            userId = user.id,
            name   = user.name
        )
    }

    override suspend fun login(req: LoginRequest): PreAuthResponse {
        val (user, hashed) = userRepo.findByEmail(req.email)
            ?: throw IllegalArgumentException("Credenciales inválidas.")
        if (!BCrypt.checkpw(req.password, hashed))
            throw IllegalArgumentException("Credenciales inválidas.")

        val code      = generateCode()
        val expiresAt = Instant.now().plusSeconds(otpExpirySeconds)

        otpRepo.save(
            userId    = user.id,
            code      = code,
            expiresAt = expiresAt
        )

        emailService.sendOtp(
            toEmail = user.email,
            toName  = user.name,
            code    = code
        )

        return PreAuthResponse(
            preAuthToken = buildPreAuthJwt(user.id, user.email)
        )
    }

    override suspend fun verifyOtp(req: VerifyOtpRequest): AuthResponse {
        // Validar formato del código
        if (req.code.length != 6 || !req.code.all { it.isDigit() })
            throw IllegalArgumentException("El código debe ser de 6 dígitos numéricos.")

        val userId = extractUserIdFromPreAuthToken(req.preAuthToken)

        val valid = otpRepo.validate(userId = userId, code = req.code)
        if (!valid)
            throw IllegalArgumentException("Código incorrecto, expirado o ya utilizado.")

        otpRepo.markUsed(userId = userId, code = req.code)

        val user = userRepo.findById(userId)
            ?: throw IllegalStateException("Usuario no encontrado.")

        return AuthResponse(
            token  = buildSessionJwt(user.id, user.email),
            userId = user.id,
            name   = user.name
        )
    }

    private fun buildSessionJwt(userId: Int, email: String): String =
        JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiration))
            .sign(Algorithm.HMAC256(jwtSecret))

    private fun buildPreAuthJwt(userId: Int, email: String): String =
        JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("type", "pre_auth")
            .withJWTId(UUID.randomUUID().toString())
            .withExpiresAt(Date(System.currentTimeMillis() + preAuthExpiry))
            .sign(Algorithm.HMAC256(jwtSecret))

    private fun extractUserIdFromPreAuthToken(token: String): Int {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(jwtIssuer)
                .withAudience(jwtAudience)
                .withClaim("type", "pre_auth")
                .build()
            verifier.verify(token).getClaim("userId").asInt()
                ?: throw IllegalArgumentException("Token inválido.")
        } catch (e: JWTVerificationException) {
            throw IllegalArgumentException("Token expirado o inválido. Inicia sesión de nuevo.")
        }
    }

    private fun generateCode(): String = (SecureRandom().nextInt(900_000) + 100_000).toString()
}
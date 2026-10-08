package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.application.port.RefreshTokenGenerator
import com.easyguide.backend.shared.application.port.TokenIssuer
import com.easyguide.backend.user.domain.model.RefreshToken
import com.easyguide.backend.user.domain.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

/** Выдаёт пару access JWT + refresh-токен. Общий для регистрации, входа и обновления токенов. */
@Component
class AuthTokensIssuer(
    private val tokenIssuer: TokenIssuer,
    private val refreshTokenGenerator: RefreshTokenGenerator,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val clock: Clock,
    @Value($$"${app.jwt.refresh-expiration-days:30}") private val refreshExpirationDays: Long,
) {

    fun issue(userId: UUID): AuthTokens {
        val now = clock.now()
        val rawRefreshToken = refreshTokenGenerator.generate()

        refreshTokenRepository.save(
            RefreshToken(
                id = UUID.randomUUID(),
                userId = userId,
                tokenHash = refreshTokenGenerator.hash(rawRefreshToken),
                expiresAt = now.plus(Duration.ofDays(refreshExpirationDays)),
                createdAt = now,
            ),
        )

        return AuthTokens(
            accessToken = tokenIssuer.issue(userId),
            refreshToken = rawRefreshToken,
        )
    }
}

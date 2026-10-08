package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.application.port.RefreshTokenGenerator
import com.easyguide.backend.user.application.dto.AuthResult
import com.easyguide.backend.user.application.dto.RefreshTokenCommand
import com.easyguide.backend.user.application.dto.toResult
import com.easyguide.backend.user.domain.exception.InvalidRefreshTokenException
import com.easyguide.backend.user.domain.repository.RefreshTokenRepository
import com.easyguide.backend.user.domain.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * Ротация refresh-токена: старый отзывается, выдаётся новая пара токенов.
 *
 * Намеренно без @Transactional: отзыв всех сессий при повторном использовании токена
 * должен сохраниться, даже когда следом кидается исключение.
 */
@Service
class RefreshTokenUseCase(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenGenerator: RefreshTokenGenerator,
    private val userRepository: UserRepository,
    private val authTokensIssuer: AuthTokensIssuer,
    private val clock: Clock,
) {

    fun execute(command: RefreshTokenCommand): AuthResult {
        val now = clock.now()
        val stored = refreshTokenRepository.findByTokenHash(refreshTokenGenerator.hash(command.refreshToken))
            ?: throw InvalidRefreshTokenException()

        if (stored.isRevoked) {
            // уже использованный токен предъявлен повторно — вероятна утечка, завершаем все сессии пользователя
            refreshTokenRepository.revokeAllByUserId(stored.userId, now)
            throw InvalidRefreshTokenException()
        }

        if (stored.isExpired(now)) {
            throw InvalidRefreshTokenException()
        }

        // проиграл гонку параллельному refresh с тем же токеном
        if (!refreshTokenRepository.tryRevoke(stored.id, now)) {
            throw InvalidRefreshTokenException()
        }

        val user = userRepository.findById(stored.userId)
            ?: throw InvalidRefreshTokenException()

        val tokens = authTokensIssuer.issue(user.id)

        return AuthResult(token = tokens.accessToken, refreshToken = tokens.refreshToken, user = user.toResult())
    }
}

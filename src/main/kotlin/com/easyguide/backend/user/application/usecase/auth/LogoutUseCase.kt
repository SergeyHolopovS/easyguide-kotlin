package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.application.port.RefreshTokenGenerator
import com.easyguide.backend.user.application.dto.RefreshTokenCommand
import com.easyguide.backend.user.domain.repository.RefreshTokenRepository
import org.springframework.stereotype.Service

/** Отзывает refresh-токен. Идемпотентен: неизвестный или уже отозванный токен не считается ошибкой. */
@Service
class LogoutUseCase(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenGenerator: RefreshTokenGenerator,
    private val clock: Clock,
) {

    fun execute(command: RefreshTokenCommand) {
        val stored = refreshTokenRepository.findByTokenHash(refreshTokenGenerator.hash(command.refreshToken))
            ?: return

        refreshTokenRepository.tryRevoke(stored.id, clock.now())
    }
}

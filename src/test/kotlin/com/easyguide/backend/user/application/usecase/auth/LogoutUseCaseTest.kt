package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.FakeClock
import com.easyguide.backend.shared.application.port.FakeRefreshTokenGenerator
import com.easyguide.backend.shared.application.port.FakeTokenIssuer
import com.easyguide.backend.user.application.dto.RefreshTokenCommand
import com.easyguide.backend.user.domain.repository.InMemoryRefreshTokenRepository
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue

class LogoutUseCaseTest {

    private val clock = FakeClock()
    private val refreshTokenRepository = InMemoryRefreshTokenRepository()
    private val refreshTokenGenerator = FakeRefreshTokenGenerator()
    private val authTokensIssuer = AuthTokensIssuer(
        FakeTokenIssuer(), refreshTokenGenerator, refreshTokenRepository, clock, refreshExpirationDays = 30,
    )
    private val useCase = LogoutUseCase(refreshTokenRepository, refreshTokenGenerator, clock)

    @Test
    fun `отзывает refresh-токен`() {
        val tokens = authTokensIssuer.issue(UUID.randomUUID())

        useCase.execute(RefreshTokenCommand(tokens.refreshToken))

        assertTrue(refreshTokenRepository.all().single().isRevoked)
    }

    @Test
    fun `неизвестный токен игнорируется`() {
        useCase.execute(RefreshTokenCommand("unknown"))
    }
}

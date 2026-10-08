package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.application.port.FakeRefreshTokenGenerator
import com.easyguide.backend.shared.application.port.FakeTokenIssuer
import com.easyguide.backend.user.application.dto.RefreshTokenCommand
import com.easyguide.backend.user.domain.exception.InvalidRefreshTokenException
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.InMemoryRefreshTokenRepository
import com.easyguide.backend.user.domain.repository.InMemoryUserRepository
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RefreshTokenUseCaseTest {

    private class MutableClock(var current: Instant = Instant.parse("2026-01-01T00:00:00Z")) : Clock {
        override fun now(): Instant = current
    }

    private val clock = MutableClock()
    private val userRepository = InMemoryUserRepository()
    private val refreshTokenRepository = InMemoryRefreshTokenRepository()
    private val refreshTokenGenerator = FakeRefreshTokenGenerator()
    private val authTokensIssuer = AuthTokensIssuer(
        FakeTokenIssuer(), refreshTokenGenerator, refreshTokenRepository, clock, refreshExpirationDays = 30,
    )
    private val useCase = RefreshTokenUseCase(
        refreshTokenRepository, refreshTokenGenerator, userRepository, authTokensIssuer, clock,
    )

    private val user = userRepository.save(
        User(
            id = UUID.randomUUID(),
            name = "Иван Иванов",
            email = "ivan@example.com",
            passwordHash = "hash",
            phone = null,
            isGuide = false,
            avatarUrl = null,
            bio = null,
            city = null,
            languages = emptyList(),
            createdAt = clock.now(),
        ),
    )

    @Test
    fun `выдаёт новую пару токенов и отзывает старый refresh-токен`() {
        val initial = authTokensIssuer.issue(user.id)

        val result = useCase.execute(RefreshTokenCommand(initial.refreshToken))

        assertEquals("token:${user.id}", result.token)
        assertNotEquals(initial.refreshToken, result.refreshToken)
        assertEquals(user.id, result.user.id)
        assertTrue(refreshTokenRepository.findByTokenHash("hashed:${initial.refreshToken}")!!.isRevoked)
    }

    @Test
    fun `неизвестный токен кидает InvalidRefreshTokenException`() {
        assertFailsWith<InvalidRefreshTokenException> {
            useCase.execute(RefreshTokenCommand("unknown"))
        }
    }

    @Test
    fun `истёкший токен кидает InvalidRefreshTokenException`() {
        val initial = authTokensIssuer.issue(user.id)
        clock.current = clock.current.plus(Duration.ofDays(31))

        assertFailsWith<InvalidRefreshTokenException> {
            useCase.execute(RefreshTokenCommand(initial.refreshToken))
        }
    }

    @Test
    fun `повторное использование токена отзывает все сессии пользователя`() {
        val initial = authTokensIssuer.issue(user.id)
        val otherSession = authTokensIssuer.issue(user.id)
        val rotated = useCase.execute(RefreshTokenCommand(initial.refreshToken))

        assertFailsWith<InvalidRefreshTokenException> {
            useCase.execute(RefreshTokenCommand(initial.refreshToken))
        }

        assertTrue(refreshTokenRepository.all().all { it.isRevoked })
        assertFailsWith<InvalidRefreshTokenException> {
            useCase.execute(RefreshTokenCommand(rotated.refreshToken))
        }
        assertFailsWith<InvalidRefreshTokenException> {
            useCase.execute(RefreshTokenCommand(otherSession.refreshToken))
        }
    }
}

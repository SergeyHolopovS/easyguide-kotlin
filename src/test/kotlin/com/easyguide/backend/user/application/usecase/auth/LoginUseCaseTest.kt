package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.FakePasswordHasher
import com.easyguide.backend.shared.application.port.FakeTokenIssuer
import com.easyguide.backend.user.application.dto.LoginCommand
import com.easyguide.backend.user.domain.exception.InvalidCredentialsException
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.InMemoryUserRepository
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoginUseCaseTest {

    private val userRepository = InMemoryUserRepository()
    private val passwordHasher = FakePasswordHasher()
    private val tokenIssuer = FakeTokenIssuer()
    private val useCase = LoginUseCase(userRepository, passwordHasher, tokenIssuer)

    private fun registerUser(email: String, rawPassword: String): User {
        val user = User(
            id = UUID.randomUUID(),
            name = "Иван Иванов",
            email = email,
            passwordHash = passwordHasher.hash(rawPassword),
            phone = null,
            isGuide = false,
            avatarUrl = null,
            bio = null,
            city = null,
            languages = emptyList(),
            createdAt = Instant.now(),
        )
        return userRepository.save(user)
    }

    @Test
    fun `успешный вход возвращает токен и данные пользователя`() {
        val user = registerUser("ivan@example.com", "secret123")

        val result = useCase.execute(LoginCommand(email = "ivan@example.com", password = "secret123"))

        assertEquals(user.id, result.user.id)
        assertEquals("token:${user.id}", result.token)
    }

    @Test
    fun `неверный пароль кидает InvalidCredentialsException`() {
        registerUser("ivan@example.com", "secret123")

        assertFailsWith<InvalidCredentialsException> {
            useCase.execute(LoginCommand(email = "ivan@example.com", password = "wrong"))
        }
    }

    @Test
    fun `несуществующий email кидает ту же InvalidCredentialsException`() {
        assertFailsWith<InvalidCredentialsException> {
            useCase.execute(LoginCommand(email = "nobody@example.com", password = "whatever"))
        }
    }
}

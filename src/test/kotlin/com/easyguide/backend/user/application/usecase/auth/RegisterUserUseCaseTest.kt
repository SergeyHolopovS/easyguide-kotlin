package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.FakeClock
import com.easyguide.backend.shared.application.port.FakePasswordHasher
import com.easyguide.backend.shared.application.port.FakeTokenIssuer
import com.easyguide.backend.user.application.dto.RegisterUserCommand
import com.easyguide.backend.user.domain.exception.EmailAlreadyTakenException
import com.easyguide.backend.user.domain.repository.InMemoryUserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class RegisterUserUseCaseTest {

    private val userRepository = InMemoryUserRepository()
    private val passwordHasher = FakePasswordHasher()
    private val tokenIssuer = FakeTokenIssuer()
    private val clock = FakeClock()
    private val useCase = RegisterUserUseCase(userRepository, passwordHasher, tokenIssuer, clock)

    private fun command(email: String = "ivan@example.com") = RegisterUserCommand(
        name = "Иван Иванов",
        email = email,
        password = "secret123",
        phone = "+79990000000",
    )

    @Test
    fun `регистрирует пользователя, хеширует пароль через порт и выдаёт токен`() {
        val result = useCase.execute(command())

        val saved = userRepository.findByEmail("ivan@example.com")
        assertEquals("Иван Иванов", saved?.name)
        assertEquals("hashed:secret123", saved?.passwordHash)
        assertNotEquals("secret123", saved?.passwordHash)
        assertEquals("token:${saved?.id}", result.token)
        assertEquals(saved?.id, result.user.id)
        assertEquals(saved?.email, result.user.email)
    }

    @Test
    fun `кидает EmailAlreadyTakenException если email уже занят`() {
        useCase.execute(command())

        assertFailsWith<EmailAlreadyTakenException> {
            useCase.execute(command())
        }
    }
}

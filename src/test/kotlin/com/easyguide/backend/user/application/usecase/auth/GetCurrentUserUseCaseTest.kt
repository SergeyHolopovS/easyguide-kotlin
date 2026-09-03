package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.InMemoryUserRepository
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetCurrentUserUseCaseTest {

    private val userRepository = InMemoryUserRepository()
    private val useCase = GetCurrentUserUseCase(userRepository)

    @Test
    fun `возвращает пользователя по id`() {
        val user = User(
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
            createdAt = Instant.now(),
        )
        userRepository.save(user)

        val result = useCase.execute(user.id)

        assertEquals(user.id, result.id)
        assertEquals(user.email, result.email)
    }

    @Test
    fun `кидает EntityNotFoundException если пользователь не найден`() {
        assertFailsWith<EntityNotFoundException> {
            useCase.execute(UUID.randomUUID())
        }
    }
}

package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.application.dto.CreateTourCommand
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.InMemoryUserRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateTourUseCaseTest {

    private val tourRepository = InMemoryTourRepository()
    private val userRepository = InMemoryUserRepository()
    private val useCase = CreateTourUseCase(tourRepository, userRepository)

    private fun user(isGuide: Boolean): User {
        val user = User(
            id = UUID.randomUUID(),
            name = "Иван",
            email = "user-${UUID.randomUUID()}@example.com",
            passwordHash = "hash",
            phone = null,
            isGuide = isGuide,
            avatarUrl = null,
            bio = if (isGuide) "Опытный гид" else null,
            city = if (isGuide) "Москва" else null,
            languages = if (isGuide) listOf("ru") else emptyList(),
            createdAt = Instant.now(),
        )
        userRepository.save(user)
        return user
    }

    private fun command() = CreateTourCommand(
        title = "Прогулка по центру",
        description = "Описание тура подлиннее пятидесяти символов для валидации полей",
        city = "Москва",
        category = TourCategory.WALKING,
        meetingPoint = "Красная площадь",
        timezone = ZoneId.of("Europe/Moscow"),
        durationMinutes = 120,
        price = BigDecimal("1000.00"),
        maxPeople = 10,
    )

    @Test
    fun `гид успешно создаёт тур в статусе DRAFT`() {
        val guide = user(isGuide = true)

        val result = useCase.execute(guide.id, command())

        assertEquals(TourStatus.DRAFT, result.status)
        assertEquals(guide.id, result.guideId)
        assertEquals(1, tourRepository.findByGuideId(guide.id).size)
    }

    @Test
    fun `не-гид не может создать тур`() {
        val notGuide = user(isGuide = false)

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(notGuide.id, command())
        }
    }
}

package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.application.dto.UpdateTourCommand
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateTourUseCaseTest {

    private val tourRepository = InMemoryTourRepository()
    private val useCase = UpdateTourUseCase(tourRepository)

    private fun tour(guideId: UUID = UUID.randomUUID()): Tour {
        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guideId,
            title = "Старое название",
            description = "Описание тура подлиннее пятидесяти символов для валидации полей",
            city = "Москва",
            category = TourCategory.WALKING,
            meetingPoint = "Точка встречи",
            timezone = ZoneId.of("Europe/Moscow"),
            durationMinutes = 60,
            price = BigDecimal("500.00"),
            maxPeople = 5,
        )
        tourRepository.save(tour)
        return tour
    }

    private fun command() = UpdateTourCommand(
        title = "Новое название",
        description = "Новое описание тура подлиннее пятидесяти символов для валидации",
        city = "Санкт-Петербург",
        category = TourCategory.CULTURE,
        meetingPoint = "Новая точка встречи",
        timezone = ZoneId.of("Europe/Moscow"),
        durationMinutes = 90,
        price = BigDecimal("700.00"),
        maxPeople = 8,
    )

    @Test
    fun `владелец успешно обновляет тур`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId)

        val result = useCase.execute(tour.id, guideId, command())

        assertEquals("Новое название", result.title)
        assertEquals("Санкт-Петербург", result.city)
    }

    @Test
    fun `чужой тур обновить нельзя`() {
        val tour = tour()

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(tour.id, UUID.randomUUID(), command())
        }
    }
}

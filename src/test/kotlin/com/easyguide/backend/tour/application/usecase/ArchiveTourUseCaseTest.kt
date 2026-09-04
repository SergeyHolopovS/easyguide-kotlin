package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ArchiveTourUseCaseTest {

    private val tourRepository = InMemoryTourRepository()
    private val useCase = ArchiveTourUseCase(tourRepository)

    private fun tour(guideId: UUID = UUID.randomUUID()): Tour {
        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guideId,
            title = "Тур",
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

    @Test
    fun `владелец успешно архивирует тур`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId)

        val result = useCase.execute(tour.id, guideId)

        assertEquals(TourStatus.ARCHIVED, result.status)
    }

    @Test
    fun `чужой тур архивировать нельзя`() {
        val tour = tour()

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(tour.id, UUID.randomUUID())
        }
    }
}

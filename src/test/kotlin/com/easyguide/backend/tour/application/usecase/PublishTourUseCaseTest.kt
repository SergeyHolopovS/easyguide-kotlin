package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.domain.exception.TourNotReadyException
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourPhoto
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PublishTourUseCaseTest {

    private val tourRepository = InMemoryTourRepository()
    private val useCase = PublishTourUseCase(tourRepository)

    private fun tour(
        guideId: UUID = UUID.randomUUID(),
        description: String = "Описание тура подлиннее пятидесяти символов для валидации полей",
        price: BigDecimal? = BigDecimal("500.00"),
        photos: List<TourPhoto> = listOf(TourPhoto(id = UUID.randomUUID(), url = "https://example.com/1.jpg")),
    ): Tour {
        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guideId,
            title = "Тур",
            description = description,
            city = "Москва",
            category = TourCategory.WALKING,
            meetingPoint = "Точка встречи",
            timezone = ZoneId.of("Europe/Moscow"),
            durationMinutes = 60,
            price = price,
            maxPeople = 5,
            photos = photos,
        )
        tourRepository.save(tour)
        return tour
    }

    @Test
    fun `владелец успешно публикует заполненный тур`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId)

        val result = useCase.execute(tour.id, guideId)

        assertEquals(TourStatus.PUBLISHED, result.status)
    }

    @Test
    fun `публикация неготового тура кидает TourNotReadyException`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId, description = "коротко", price = null, photos = emptyList())

        assertFailsWith<TourNotReadyException> {
            useCase.execute(tour.id, guideId)
        }
    }

    @Test
    fun `чужой тур опубликовать нельзя`() {
        val tour = tour()

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(tour.id, UUID.randomUUID())
        }
    }
}

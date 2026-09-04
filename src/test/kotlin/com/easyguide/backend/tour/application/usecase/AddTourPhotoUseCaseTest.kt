package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.domain.exception.TourPhotoLimitExceededException
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourPhoto
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddTourPhotoUseCaseTest {

    private val tourRepository = InMemoryTourRepository()
    private val useCase = AddTourPhotoUseCase(tourRepository)

    private fun tour(guideId: UUID = UUID.randomUUID(), photos: List<TourPhoto> = emptyList()): Tour {
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
            photos = photos,
        )
        tourRepository.save(tour)
        return tour
    }

    @Test
    fun `владелец успешно добавляет фото`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId)

        val result = useCase.execute(tour.id, guideId, "https://example.com/1.jpg")

        assertEquals(1, result.photos.size)
        assertEquals("https://example.com/1.jpg", result.photos.first().url)
    }

    @Test
    fun `после 10 фото кидает TourPhotoLimitExceededException`() {
        val guideId = UUID.randomUUID()
        val existingPhotos = (0 until 10).map {
            TourPhoto(id = UUID.randomUUID(), url = "https://example.com/$it.jpg", sortOrder = it)
        }
        val tour = tour(guideId, existingPhotos)

        assertFailsWith<TourPhotoLimitExceededException> {
            useCase.execute(tour.id, guideId, "https://example.com/11.jpg")
        }
    }

    @Test
    fun `чужой тур фото добавить нельзя`() {
        val tour = tour()

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(tour.id, UUID.randomUUID(), "https://example.com/1.jpg")
        }
    }
}

package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
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

class RemoveTourPhotoUseCaseTest {

    private val tourRepository = InMemoryTourRepository()
    private val useCase = RemoveTourPhotoUseCase(tourRepository)

    private fun tour(guideId: UUID = UUID.randomUUID(), photos: List<TourPhoto>): Tour {
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
    fun `владелец успешно удаляет фото`() {
        val guideId = UUID.randomUUID()
        val photo = TourPhoto(id = UUID.randomUUID(), url = "https://example.com/1.jpg")
        val tour = tour(guideId, listOf(photo))

        val result = useCase.execute(tour.id, guideId, photo.id)

        assertEquals(0, result.photos.size)
    }

    @Test
    fun `чужой тур фото удалить нельзя`() {
        val photo = TourPhoto(id = UUID.randomUUID(), url = "https://example.com/1.jpg")
        val tour = tour(photos = listOf(photo))

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(tour.id, UUID.randomUUID(), photo.id)
        }
    }
}

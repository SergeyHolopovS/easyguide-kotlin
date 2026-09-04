package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.application.dto.toResult
import com.easyguide.backend.tour.domain.exception.TourPhotoLimitExceededException
import com.easyguide.backend.tour.domain.repository.TourRepository
import org.springframework.stereotype.Service
import java.util.UUID

private const val MAX_PHOTOS = 10

@Service
class AddTourPhotoUseCase(
    private val tourRepository: TourRepository,
) {

    fun execute(tourId: UUID, userId: UUID, url: String): TourResult {
        val tour = tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        requireTourOwner(tour, userId)

        if (tour.photos.size >= MAX_PHOTOS) {
            throw TourPhotoLimitExceededException(tourId, MAX_PHOTOS)
        }

        tour.addPhoto(url)

        return tourRepository.save(tour).toResult()
    }
}

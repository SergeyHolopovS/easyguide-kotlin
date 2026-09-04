package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.application.dto.toResult
import com.easyguide.backend.tour.domain.repository.TourRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RemoveTourPhotoUseCase(
    private val tourRepository: TourRepository,
) {

    fun execute(tourId: UUID, userId: UUID, photoId: UUID): TourResult {
        val tour = tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        requireTourOwner(tour, userId)

        tour.removePhoto(photoId)

        return tourRepository.save(tour).toResult()
    }
}

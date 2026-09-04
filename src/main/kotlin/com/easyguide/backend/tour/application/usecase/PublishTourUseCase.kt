package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.application.dto.toResult
import com.easyguide.backend.tour.domain.exception.TourNotReadyException
import com.easyguide.backend.tour.domain.repository.TourRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PublishTourUseCase(
    private val tourRepository: TourRepository,
) {

    /** @throws TourNotReadyException если у тура не заполнены описание/цена/фото */
    fun execute(tourId: UUID, userId: UUID): TourResult {
        val tour = tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        requireTourOwner(tour, userId)

        tour.publish()

        return tourRepository.save(tour).toResult()
    }
}

package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.application.dto.UpdateTourCommand
import com.easyguide.backend.tour.application.dto.toResult
import com.easyguide.backend.tour.domain.repository.TourRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateTourUseCase(
    private val tourRepository: TourRepository,
) {

    fun execute(tourId: UUID, userId: UUID, command: UpdateTourCommand): TourResult {
        val tour = tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        requireTourOwner(tour, userId)

        tour.update(
            title = command.title,
            description = command.description,
            city = command.city,
            category = command.category,
            meetingPoint = command.meetingPoint,
            timezone = command.timezone,
            durationMinutes = command.durationMinutes,
            price = command.price,
            maxPeople = command.maxPeople,
        )

        return tourRepository.save(tour).toResult()
    }
}

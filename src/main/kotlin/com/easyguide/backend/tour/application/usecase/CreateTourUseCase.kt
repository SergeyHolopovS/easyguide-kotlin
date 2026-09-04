package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.dto.CreateTourCommand
import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.application.dto.toResult
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateTourUseCase(
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository,
) {

    fun execute(guideId: UUID, command: CreateTourCommand): TourResult {
        val user = userRepository.findById(guideId)
            ?: throw EntityNotFoundException("Пользователь", guideId)

        if (!user.isGuide) {
            throw AccessDeniedDomainException("Только гид может создавать туры")
        }

        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guideId,
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

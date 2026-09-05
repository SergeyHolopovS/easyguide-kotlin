package com.easyguide.backend.tourslot.application.usecase.slot

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.usecase.requireTourOwner
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.application.dto.CreateSlotCommand
import com.easyguide.backend.tourslot.application.dto.SlotResult
import com.easyguide.backend.tourslot.application.dto.toResult
import com.easyguide.backend.tourslot.domain.exception.SlotStartInPastException
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateSlotUseCase(
    private val tourRepository: TourRepository,
    private val slotRepository: SlotRepository,
    private val clock: Clock,
) {

    fun execute(tourId: UUID, userId: UUID, command: CreateSlotCommand): SlotResult {
        val tour = tourRepository.findById(tourId)
            ?: throw EntityNotFoundException("Тур", tourId)

        requireTourOwner(tour, userId)

        if (!command.startsAt.isAfter(clock.now())) {
            throw SlotStartInPastException()
        }

        val slot = TourSlot(
            id = UUID.randomUUID(),
            tourId = tourId,
            startsAt = command.startsAt,
            capacity = command.capacity,
        )

        return slotRepository.save(slot).toResult()
    }
}

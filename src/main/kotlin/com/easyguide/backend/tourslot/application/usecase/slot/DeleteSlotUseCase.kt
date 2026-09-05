package com.easyguide.backend.tourslot.application.usecase.slot

import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.usecase.requireTourOwner
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.exception.SlotHasBookingsException
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteSlotUseCase(
    private val slotRepository: SlotRepository,
    private val tourRepository: TourRepository,
    private val bookingRepository: BookingRepository,
) {

    fun execute(slotId: UUID, userId: UUID) {
        val slot = slotRepository.findById(slotId)
            ?: throw EntityNotFoundException("Слот", slotId)

        val tour = tourRepository.findById(slot.tourId)
            ?: throw EntityNotFoundException("Тур", slot.tourId)

        requireTourOwner(tour, userId)

        if (bookingRepository.findActiveBySlot(slotId).isNotEmpty()) {
            throw SlotHasBookingsException(slotId)
        }

        slotRepository.delete(slotId)
    }
}

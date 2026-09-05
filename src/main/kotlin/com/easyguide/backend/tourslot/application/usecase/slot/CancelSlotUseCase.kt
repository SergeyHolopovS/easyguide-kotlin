package com.easyguide.backend.tourslot.application.usecase.slot

import com.easyguide.backend.booking.domain.model.CancelledBy
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.usecase.requireTourOwner
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.application.dto.SlotResult
import com.easyguide.backend.tourslot.application.dto.toResult
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CancelSlotUseCase(
    private val slotRepository: SlotRepository,
    private val tourRepository: TourRepository,
    private val bookingRepository: BookingRepository,
) {

    @Transactional
    fun execute(slotId: UUID, userId: UUID): SlotResult {
        val slot = slotRepository.findById(slotId)
            ?: throw EntityNotFoundException("Слот", slotId)

        val tour = tourRepository.findById(slot.tourId)
            ?: throw EntityNotFoundException("Тур", slot.tourId)

        requireTourOwner(tour, userId)

        slot.cancel()
        val savedSlot = slotRepository.save(slot)

        bookingRepository.findActiveBySlot(slotId).forEach { booking ->
            booking.cancel(CancelledBy.GUIDE, "Слот отменён гидом")
            bookingRepository.save(booking)
        }

        return savedSlot.toResult()
    }
}

package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.application.dto.BookingResult
import com.easyguide.backend.booking.application.dto.toResult
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.application.usecase.requireTourOwner
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RejectBookingUseCase(
    private val bookingRepository: BookingRepository,
    private val slotRepository: SlotRepository,
    private val tourRepository: TourRepository,
) {

    @Transactional
    fun execute(bookingId: UUID, actorUserId: UUID): BookingResult {
        val booking = bookingRepository.findById(bookingId)
            ?: throw EntityNotFoundException("Бронирование", bookingId)

        val slot = slotRepository.findById(booking.slotId)
            ?: throw EntityNotFoundException("Слот", booking.slotId)

        val tour = tourRepository.findById(slot.tourId)
            ?: throw EntityNotFoundException("Тур", slot.tourId)

        requireTourOwner(tour, actorUserId)

        booking.reject()
        val saved = bookingRepository.save(booking)

        slotRepository.releaseSeats(booking.slotId, booking.seats)

        return saved.toResult()
    }
}

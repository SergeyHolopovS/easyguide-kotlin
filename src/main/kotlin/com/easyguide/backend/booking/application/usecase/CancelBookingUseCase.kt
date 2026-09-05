package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.application.dto.BookingResult
import com.easyguide.backend.booking.application.dto.CancelBookingCommand
import com.easyguide.backend.booking.application.dto.toResult
import com.easyguide.backend.booking.domain.exception.BookingCancelAfterSlotStartException
import com.easyguide.backend.booking.domain.model.CancelledBy
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CancelBookingUseCase(
    private val bookingRepository: BookingRepository,
    private val slotRepository: SlotRepository,
    private val tourRepository: TourRepository,
    private val clock: Clock,
) {

    @Transactional
    fun execute(bookingId: UUID, actorUserId: UUID, command: CancelBookingCommand): BookingResult {
        val booking = bookingRepository.findById(bookingId)
            ?: throw EntityNotFoundException("Бронирование", bookingId)

        val slot = slotRepository.findById(booking.slotId)
            ?: throw EntityNotFoundException("Слот", booking.slotId)

        val tour = tourRepository.findById(slot.tourId)
            ?: throw EntityNotFoundException("Тур", slot.tourId)

        val cancelledBy = when (actorUserId) {
            booking.userId -> CancelledBy.TOURIST
            tour.guideId -> CancelledBy.GUIDE
            else -> throw AccessDeniedDomainException("Отменить бронирование может только турист или гид тура")
        }

        if (!clock.now().isBefore(slot.startsAt)) {
            throw BookingCancelAfterSlotStartException(bookingId)
        }

        booking.cancel(cancelledBy, command.reason)
        val saved = bookingRepository.save(booking)

        slotRepository.releaseSeats(booking.slotId, booking.seats)

        return saved.toResult()
    }
}

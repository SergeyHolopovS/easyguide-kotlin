package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.application.dto.BookingResult
import com.easyguide.backend.booking.application.dto.CreateBookingCommand
import com.easyguide.backend.booking.application.dto.toResult
import com.easyguide.backend.booking.domain.exception.AlreadyBookedException
import com.easyguide.backend.booking.domain.exception.OwnTourBookingException
import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.exception.SlotFullException
import com.easyguide.backend.tourslot.domain.exception.SlotNotBookableException
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class CreateBookingUseCase(
    private val slotRepository: SlotRepository,
    private val tourRepository: TourRepository,
    private val bookingRepository: BookingRepository,
    private val clock: Clock,
) {

    @Transactional
    fun execute(userId: UUID, command: CreateBookingCommand): BookingResult {
        val slot = slotRepository.findById(command.slotId)
            ?.takeIf { !it.isCancelled }
            ?: throw EntityNotFoundException("Слот", command.slotId)

        if (!slot.isBookable(clock.now())) {
            throw SlotNotBookableException(slot.id)
        }

        val tour = tourRepository.findById(slot.tourId)
            ?: throw EntityNotFoundException("Тур", slot.tourId)

        if (tour.guideId == userId) {
            throw OwnTourBookingException()
        }

        if (bookingRepository.existsActiveByUserAndSlot(userId, slot.id)) {
            throw AlreadyBookedException(slot.id)
        }

        if (!slotRepository.tryReserveSeats(slot.id, command.seats)) {
            throw SlotFullException(slot.id)
        }

        val price = checkNotNull(tour.price) { "У тура id=${tour.id} не задана цена" }

        val booking = Booking(
            id = UUID.randomUUID(),
            slotId = slot.id,
            userId = userId,
            seats = command.seats,
            totalPrice = price * BigDecimal(command.seats),
            contactPhone = command.contactPhone,
            comment = command.comment,
            createdAt = clock.now(),
        )

        return bookingRepository.save(booking).toResult()
    }
}

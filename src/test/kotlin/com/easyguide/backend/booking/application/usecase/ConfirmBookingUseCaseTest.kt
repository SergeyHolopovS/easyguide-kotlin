package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.domain.exception.InvalidBookingStatusException
import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.repository.InMemoryBookingRepository
import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.domain.repository.InMemorySlotRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfirmBookingUseCaseTest {

    private val bookingRepository = InMemoryBookingRepository()
    private val slotRepository = InMemorySlotRepository()
    private val tourRepository = InMemoryTourRepository()
    private val useCase = ConfirmBookingUseCase(bookingRepository, slotRepository, tourRepository)

    private fun tour(guideId: UUID = UUID.randomUUID()): Tour {
        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guideId,
            title = "Тур",
            description = "Описание тура подлиннее пятидесяти символов для валидации полей",
            city = "Москва",
            category = TourCategory.WALKING,
            meetingPoint = "Точка встречи",
            timezone = ZoneId.of("Europe/Moscow"),
            durationMinutes = 60,
            price = BigDecimal("1000.00"),
            maxPeople = 5,
            status = TourStatus.PUBLISHED,
        )
        tourRepository.save(tour)
        return tour
    }

    private fun slot(tourId: UUID): TourSlot {
        val slot = TourSlot(
            id = UUID.randomUUID(),
            tourId = tourId,
            startsAt = Instant.now().plus(3, ChronoUnit.HOURS),
            capacity = 5,
        )
        slotRepository.save(slot)
        return slot
    }

    private fun booking(slotId: UUID, status: BookingStatus = BookingStatus.PENDING): Booking {
        val booking = Booking(
            id = UUID.randomUUID(),
            slotId = slotId,
            userId = UUID.randomUUID(),
            seats = 2,
            totalPrice = BigDecimal("2000.00"),
            contactPhone = null,
            comment = null,
            createdAt = Instant.now(),
            status = status,
        )
        bookingRepository.save(booking)
        return booking
    }

    @Test
    fun `гид успешно подтверждает бронь`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId)
        val slot = slot(tour.id)
        val booking = booking(slot.id)

        val result = useCase.execute(booking.id, guideId)

        assertEquals(BookingStatus.CONFIRMED, result.status)
    }

    @Test
    fun `чужая бронь (не гид тура) кидает AccessDeniedDomainException`() {
        val tour = tour()
        val slot = slot(tour.id)
        val booking = booking(slot.id)

        assertFailsWith<AccessDeniedDomainException> {
            useCase.execute(booking.id, UUID.randomUUID())
        }
    }

    @Test
    fun `неверный исходный статус кидает InvalidBookingStatusException`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId)
        val slot = slot(tour.id)
        val booking = booking(slot.id, status = BookingStatus.REJECTED)

        assertFailsWith<InvalidBookingStatusException> {
            useCase.execute(booking.id, guideId)
        }
    }
}

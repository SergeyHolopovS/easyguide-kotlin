package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.booking.application.dto.CreateBookingCommand
import com.easyguide.backend.booking.domain.exception.AlreadyBookedException
import com.easyguide.backend.booking.domain.exception.OwnTourBookingException
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.repository.InMemoryBookingRepository
import com.easyguide.backend.shared.application.port.FakeClock
import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.InMemoryTourRepository
import com.easyguide.backend.tourslot.domain.exception.SlotFullException
import com.easyguide.backend.tourslot.domain.exception.SlotNotBookableException
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

class CreateBookingUseCaseTest {

    private val slotRepository = InMemorySlotRepository()
    private val tourRepository = InMemoryTourRepository()
    private val bookingRepository = InMemoryBookingRepository()
    private val clock = FakeClock(Instant.parse("2026-01-01T00:00:00Z"))
    private val useCase = CreateBookingUseCase(slotRepository, tourRepository, bookingRepository, clock)

    private fun tour(guideId: UUID = UUID.randomUUID(), price: BigDecimal? = BigDecimal("1000.00")): Tour {
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
            price = price,
            maxPeople = 5,
            status = TourStatus.PUBLISHED,
        )
        tourRepository.save(tour)
        return tour
    }

    private fun slot(
        tourId: UUID,
        startsAt: Instant = clock.now().plus(3, ChronoUnit.HOURS),
        capacity: Int = 5,
        bookedSeats: Int = 0,
        isCancelled: Boolean = false,
    ): TourSlot {
        val slot = TourSlot(
            id = UUID.randomUUID(),
            tourId = tourId,
            startsAt = startsAt,
            capacity = capacity,
            bookedSeats = bookedSeats,
            isCancelled = isCancelled,
        )
        slotRepository.save(slot)
        return slot
    }

    private fun command(slotId: UUID, seats: Int = 2) = CreateBookingCommand(
        slotId = slotId,
        seats = seats,
        contactPhone = "+79990000000",
        comment = null,
    )

    @Test
    fun `успешное бронирование создаёт PENDING с рассчитанной ценой`() {
        val tour = tour()
        val slot = slot(tour.id)
        val userId = UUID.randomUUID()

        val result = useCase.execute(userId, command(slot.id, seats = 2))

        assertEquals(BookingStatus.PENDING, result.status)
        assertEquals(0, BigDecimal("2000.00").compareTo(result.totalPrice))
        assertEquals(userId, result.userId)
    }

    @Test
    fun `слот занят - tryReserveSeats вернул false кидает SlotFullException`() {
        val tour = tour()
        val slot = slot(tour.id)
        slotRepository.setTryReserveSeatsResult(false)

        assertFailsWith<SlotFullException> {
            useCase.execute(UUID.randomUUID(), command(slot.id))
        }
    }

    @Test
    fun `бронирование своего тура кидает OwnTourBookingException`() {
        val guideId = UUID.randomUUID()
        val tour = tour(guideId = guideId)
        val slot = slot(tour.id)

        assertFailsWith<OwnTourBookingException> {
            useCase.execute(guideId, command(slot.id))
        }
    }

    @Test
    fun `повторная активная бронь на тот же слот кидает AlreadyBookedException`() {
        val tour = tour()
        val slot = slot(tour.id)
        val userId = UUID.randomUUID()

        useCase.execute(userId, command(slot.id, seats = 1))

        assertFailsWith<AlreadyBookedException> {
            useCase.execute(userId, command(slot.id, seats = 1))
        }
    }

    @Test
    fun `отменённый слот считается несуществующим - EntityNotFoundException`() {
        val tour = tour()
        val slot = slot(tour.id, isCancelled = true)

        assertFailsWith<EntityNotFoundException> {
            useCase.execute(UUID.randomUUID(), command(slot.id))
        }
    }

    @Test
    fun `непригодный слот (меньше 2 часов до старта) кидает SlotNotBookableException`() {
        val tour = tour()
        val slot = slot(tour.id, startsAt = clock.now().plus(1, ChronoUnit.HOURS))

        assertFailsWith<SlotNotBookableException> {
            useCase.execute(UUID.randomUUID(), command(slot.id))
        }
    }
}

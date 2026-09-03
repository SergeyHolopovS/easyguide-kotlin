package com.easyguide.backend.booking.domain.model

import com.easyguide.backend.booking.domain.exception.InvalidBookingStatusException
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookingTest {

    private fun booking(status: BookingStatus = BookingStatus.PENDING): Booking = Booking(
        id = UUID.randomUUID(),
        slotId = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        seats = 2,
        totalPrice = BigDecimal("200.00"),
        contactPhone = null,
        comment = null,
        createdAt = Instant.now(),
        status = status,
    )

    @Test
    fun `confirm переводит PENDING в CONFIRMED`() {
        val booking = booking(BookingStatus.PENDING)

        booking.confirm()

        assertEquals(BookingStatus.CONFIRMED, booking.status)
    }

    @Test
    fun `confirm кидает исключение если бронирование уже CONFIRMED`() {
        val booking = booking(BookingStatus.CONFIRMED)

        assertFailsWith<InvalidBookingStatusException> { booking.confirm() }
    }

    @Test
    fun `cancel кидает исключение из статуса COMPLETED`() {
        val booking = booking(BookingStatus.COMPLETED)

        assertFailsWith<InvalidBookingStatusException> {
            booking.cancel(CancelledBy.TOURIST, "Передумал")
        }
    }

    @Test
    fun `complete переводит CONFIRMED в COMPLETED`() {
        val booking = booking(BookingStatus.CONFIRMED)

        booking.complete()

        assertEquals(BookingStatus.COMPLETED, booking.status)
    }

    @Test
    fun `complete кидает исключение из статуса отличного от CONFIRMED`() {
        val booking = booking(BookingStatus.PENDING)

        assertFailsWith<InvalidBookingStatusException> { booking.complete() }
    }
}

package com.easyguide.backend.booking.domain.repository

import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.domain.model.BookingStatus
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val ACTIVE_STATUSES = setOf(BookingStatus.PENDING, BookingStatus.CONFIRMED)

class InMemoryBookingRepository : BookingRepository {

    private val storage = ConcurrentHashMap<UUID, Booking>()

    override fun findById(id: UUID): Booking? = storage[id]

    override fun existsActiveByUserAndSlot(userId: UUID, slotId: UUID): Boolean =
        storage.values.any { it.userId == userId && it.slotId == slotId && it.status in ACTIVE_STATUSES }

    override fun findActiveBySlot(slotId: UUID): List<Booking> =
        storage.values.filter { it.slotId == slotId && it.status in ACTIVE_STATUSES }

    override fun findConfirmedFinishedBefore(instant: Instant, limit: Int): List<Booking> =
        storage.values.filter { it.status == BookingStatus.CONFIRMED }.take(limit)

    override fun save(booking: Booking): Booking {
        storage[booking.id] = booking
        return booking
    }
}

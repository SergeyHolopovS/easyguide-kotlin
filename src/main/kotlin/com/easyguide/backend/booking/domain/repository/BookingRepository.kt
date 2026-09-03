package com.easyguide.backend.booking.domain.repository

import com.easyguide.backend.booking.domain.model.Booking
import java.time.Instant
import java.util.UUID

interface BookingRepository {
    fun findById(id: UUID): Booking?
    fun existsActiveByUserAndSlot(userId: UUID, slotId: UUID): Boolean
    fun findActiveBySlot(slotId: UUID): List<Booking>
    fun findConfirmedFinishedBefore(instant: Instant): List<Booking>
    fun save(booking: Booking): Booking
}

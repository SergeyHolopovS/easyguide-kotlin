package com.easyguide.backend.tourslot.domain.model

import com.easyguide.backend.tourslot.domain.exception.SlotFullException
import com.easyguide.backend.tourslot.domain.exception.SlotNotBookableException
import java.time.Duration
import java.time.Instant
import java.util.UUID

private val MIN_BOOKING_LEAD_TIME: Duration = Duration.ofHours(2)

class TourSlot(
    val id: UUID,
    val tourId: UUID,
    val startsAt: Instant,
    val capacity: Int,
    bookedSeats: Int = 0,
    isCancelled: Boolean = false,
) {

    var bookedSeats: Int = bookedSeats
        private set

    var isCancelled: Boolean = isCancelled
        private set

    val availableSeats: Int
        get() = capacity - bookedSeats

    init {
        require(capacity > 0) { "Вместимость должна быть положительной" }
        require(bookedSeats in 0..capacity) { "Число забронированных мест должно быть от 0 до вместимости" }
    }

    fun isBookable(now: Instant): Boolean =
        !isCancelled && availableSeats > 0 && Duration.between(now, startsAt) > MIN_BOOKING_LEAD_TIME

    fun ensureBookable(now: Instant) {
        if (isCancelled || Duration.between(now, startsAt) <= MIN_BOOKING_LEAD_TIME) {
            throw SlotNotBookableException(id)
        }
        if (availableSeats <= 0) {
            throw SlotFullException(id)
        }
    }

    fun cancel() {
        isCancelled = true
    }

}

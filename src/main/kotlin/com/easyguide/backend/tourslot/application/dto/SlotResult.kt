package com.easyguide.backend.tourslot.application.dto

import com.easyguide.backend.tourslot.domain.model.TourSlot
import java.time.Instant
import java.util.UUID

data class SlotResult(
    val id: UUID,
    val tourId: UUID,
    val startsAt: Instant,
    val capacity: Int,
    val bookedSeats: Int,
    val availableSeats: Int,
    val isCancelled: Boolean,
)

fun TourSlot.toResult(): SlotResult = SlotResult(
    id = id,
    tourId = tourId,
    startsAt = startsAt,
    capacity = capacity,
    bookedSeats = bookedSeats,
    availableSeats = availableSeats,
    isCancelled = isCancelled,
)

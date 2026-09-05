package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.dto.SlotResult
import java.time.Instant
import java.util.UUID

data class SlotResponse(
    val id: UUID,
    val tourId: UUID,
    val startsAt: Instant,
    val capacity: Int,
    val bookedSeats: Int,
    val availableSeats: Int,
    val isCancelled: Boolean,
)

fun SlotResult.toResponse(): SlotResponse = SlotResponse(
    id = id,
    tourId = tourId,
    startsAt = startsAt,
    capacity = capacity,
    bookedSeats = bookedSeats,
    availableSeats = availableSeats,
    isCancelled = isCancelled,
)

package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.query.SlotView
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class SlotViewResponse(
    val id: UUID,
    val startsAt: Instant,
    val localDate: LocalDate,
    val localTime: LocalTime,
    val capacity: Int,
    val availableSeats: Int,
    val bookable: Boolean,
)

fun SlotView.toResponse(): SlotViewResponse = SlotViewResponse(
    id = id,
    startsAt = startsAt,
    localDate = localDate,
    localTime = localTime,
    capacity = capacity,
    availableSeats = availableSeats,
    bookable = bookable,
)

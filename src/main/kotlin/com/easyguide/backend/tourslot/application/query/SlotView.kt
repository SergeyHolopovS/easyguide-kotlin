package com.easyguide.backend.tourslot.application.query

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class SlotView(
    val id: UUID,
    val startsAt: Instant,
    val localDate: LocalDate,
    val localTime: LocalTime,
    val capacity: Int,
    val availableSeats: Int,
    val bookable: Boolean,
)

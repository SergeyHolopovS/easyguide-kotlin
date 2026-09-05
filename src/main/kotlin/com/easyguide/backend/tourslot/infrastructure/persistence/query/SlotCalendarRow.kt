package com.easyguide.backend.tourslot.infrastructure.persistence.query

import java.time.Instant
import java.util.UUID

class SlotCalendarRow(
    val id: UUID,
    val startsAt: Instant,
    val capacity: Int,
    val bookedSeats: Int,
)

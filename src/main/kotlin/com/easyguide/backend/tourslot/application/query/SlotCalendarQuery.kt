package com.easyguide.backend.tourslot.application.query

import java.time.LocalDate
import java.util.UUID

interface SlotCalendarQuery {
    fun findByTourAndRange(tourId: UUID, from: LocalDate, to: LocalDate): List<SlotView>
}

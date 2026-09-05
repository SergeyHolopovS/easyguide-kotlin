package com.easyguide.backend.tourslot.infrastructure.persistence.query

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourJpaRepository
import com.easyguide.backend.tourslot.application.query.SlotCalendarQuery
import com.easyguide.backend.tourslot.application.query.SlotView
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

private val MIN_BOOKING_LEAD_TIME: Duration = Duration.ofHours(2)

@Component
class SlotCalendarQueryAdapter(
    private val slotCalendarJpaRepository: SlotCalendarJpaRepository,
    private val tourJpaRepository: TourJpaRepository,
    private val clock: Clock,
) : SlotCalendarQuery {

    override fun findByTourAndRange(tourId: UUID, from: LocalDate, to: LocalDate): List<SlotView> {
        val timezone = tourJpaRepository.findById(tourId).orElse(null)?.timezone ?: return emptyList()
        val zoneId = ZoneId.of(timezone)

        val fromInstant = from.atStartOfDay(zoneId).toInstant()
        val toInstant = to.plusDays(1).atStartOfDay(zoneId).toInstant()
        val now = clock.now()

        return slotCalendarJpaRepository.findByTourAndRange(tourId, fromInstant, toInstant, now)
            .map { row ->
                val localDateTime = ZonedDateTime.ofInstant(row.startsAt, zoneId)
                val availableSeats = row.capacity - row.bookedSeats

                SlotView(
                    id = row.id,
                    startsAt = row.startsAt,
                    localDate = localDateTime.toLocalDate(),
                    localTime = localDateTime.toLocalTime(),
                    capacity = row.capacity,
                    availableSeats = availableSeats,
                    // отменённые слоты уже отсеяны запросом (isCancelled = false в WHERE)
                    bookable = availableSeats > 0 && Duration.between(now, row.startsAt) > MIN_BOOKING_LEAD_TIME,
                )
            }
    }
}

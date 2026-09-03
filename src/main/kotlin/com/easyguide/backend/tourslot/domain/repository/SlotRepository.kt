package com.easyguide.backend.tourslot.domain.repository

import com.easyguide.backend.tourslot.domain.model.TourSlot
import java.time.Instant
import java.util.UUID

interface SlotRepository {
    fun findById(id: UUID): TourSlot?
    fun findByTourAndRange(tourId: UUID, from: Instant, to: Instant): List<TourSlot>
    fun save(tourSlot: TourSlot): TourSlot
    fun delete(id: UUID)

    /** Атомарно увеличивает bookedSeats, если есть свободные места. Возвращает true, если места удалось занять. */
    fun tryReserveSeats(slotId: UUID, seats: Int): Boolean

    /** Атомарно уменьшает bookedSeats (не уходя ниже нуля). */
    fun releaseSeats(slotId: UUID, seats: Int)
}

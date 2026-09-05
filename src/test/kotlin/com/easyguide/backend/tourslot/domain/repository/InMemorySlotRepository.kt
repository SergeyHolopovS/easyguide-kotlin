package com.easyguide.backend.tourslot.domain.repository

import com.easyguide.backend.tourslot.domain.model.TourSlot
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemorySlotRepository(
    private var tryReserveSeatsResult: Boolean = true,
) : SlotRepository {

    private val storage = ConcurrentHashMap<UUID, TourSlot>()

    fun setTryReserveSeatsResult(value: Boolean) {
        tryReserveSeatsResult = value
    }

    override fun findById(id: UUID): TourSlot? = storage[id]

    override fun findByTourAndRange(tourId: UUID, from: Instant, to: Instant): List<TourSlot> =
        storage.values.filter { it.tourId == tourId && !it.startsAt.isBefore(from) && !it.startsAt.isAfter(to) }

    override fun save(tourSlot: TourSlot): TourSlot {
        storage[tourSlot.id] = tourSlot
        return tourSlot
    }

    override fun delete(id: UUID) {
        storage.remove(id)
    }

    override fun tryReserveSeats(slotId: UUID, seats: Int): Boolean = tryReserveSeatsResult

    val releaseSeatsCalls: MutableList<Pair<UUID, Int>> = mutableListOf()

    override fun releaseSeats(slotId: UUID, seats: Int) {
        releaseSeatsCalls.add(slotId to seats)
    }
}

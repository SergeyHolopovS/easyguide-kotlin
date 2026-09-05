package com.easyguide.backend.tourslot.infrastructure.persistence.mapper

import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.infrastructure.persistence.entity.TourSlotEntity
import java.time.Instant

fun TourSlot.toEntity(): TourSlotEntity = TourSlotEntity(
    id = id,
    tourId = tourId,
    startsAt = startsAt,
    capacity = capacity,
    bookedSeats = bookedSeats,
    isCancelled = isCancelled,
    createdAt = Instant.now(), // перезатирается Hibernate (@CreationTimestamp) при реальной вставке
)

fun TourSlotEntity.toDomain(): TourSlot = TourSlot(
    id = id,
    tourId = tourId,
    startsAt = startsAt,
    capacity = capacity,
    bookedSeats = bookedSeats,
    isCancelled = isCancelled,
)

fun TourSlotEntity.updateFrom(slot: TourSlot) {
    tourId = slot.tourId
    startsAt = slot.startsAt
    capacity = slot.capacity
    bookedSeats = slot.bookedSeats
    isCancelled = slot.isCancelled
}

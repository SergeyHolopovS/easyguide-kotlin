package com.easyguide.backend.tourslot.infrastructure.persistence.query

import com.easyguide.backend.tourslot.infrastructure.persistence.entity.TourSlotEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface SlotCalendarJpaRepository : Repository<TourSlotEntity, UUID> {

    @Query(
        "SELECT new com.easyguide.backend.tourslot.infrastructure.persistence.query.SlotCalendarRow(" +
            "s.id, s.startsAt, s.capacity, s.bookedSeats) " +
            "FROM TourSlotEntity s " +
            "WHERE s.tourId = :tourId " +
            "AND s.isCancelled = false " +
            "AND s.startsAt >= :from AND s.startsAt < :to " +
            "AND s.startsAt > :now " +
            "ORDER BY s.startsAt"
    )
    fun findByTourAndRange(
        @Param("tourId") tourId: UUID,
        @Param("from") from: Instant,
        @Param("to") to: Instant,
        @Param("now") now: Instant,
    ): List<SlotCalendarRow>
}

package com.easyguide.backend.tourslot.infrastructure.persistence.jpa

import com.easyguide.backend.tourslot.infrastructure.persistence.entity.TourSlotEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface TourSlotJpaRepository : JpaRepository<TourSlotEntity, UUID> {

    fun findByTourIdAndStartsAtBetween(tourId: UUID, from: Instant, to: Instant): List<TourSlotEntity>

    /** Атомарно занимает места, только если после этого не превышается capacity. Возвращает число изменённых строк (0 или 1). */
    @Modifying
    @Query(
        value = """
            UPDATE tour_slots
            SET booked_seats = booked_seats + :seats
            WHERE id = :slotId AND booked_seats + :seats <= capacity
        """,
        nativeQuery = true,
    )
    fun tryReserveSeats(@Param("slotId") slotId: UUID, @Param("seats") seats: Int): Int

    /** Атомарно освобождает места, не уходя ниже нуля. */
    @Modifying
    @Query(
        value = """
            UPDATE tour_slots
            SET booked_seats = GREATEST(booked_seats - :seats, 0)
            WHERE id = :slotId
        """,
        nativeQuery = true,
    )
    fun releaseSeats(@Param("slotId") slotId: UUID, @Param("seats") seats: Int): Int

}

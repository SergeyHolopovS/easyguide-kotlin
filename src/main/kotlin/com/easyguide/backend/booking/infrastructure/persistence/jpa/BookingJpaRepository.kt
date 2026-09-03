package com.easyguide.backend.booking.infrastructure.persistence.jpa

import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.infrastructure.persistence.entity.BookingEntity
import com.easyguide.backend.tourslot.infrastructure.persistence.entity.TourSlotEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface BookingJpaRepository : JpaRepository<BookingEntity, UUID> {

    fun existsByUserIdAndSlotIdAndStatusIn(
        userId: UUID,
        slotId: UUID,
        statuses: Collection<BookingStatus>,
    ): Boolean

    fun findBySlotIdAndStatusIn(
        slotId: UUID,
        statuses: Collection<BookingStatus>,
    ): List<BookingEntity>

    @Query(
        "SELECT b FROM BookingEntity b, TourSlotEntity s " +
            "WHERE s.id = b.slotId AND b.status = :status AND s.startsAt < :before"
    )
    fun findByStatusAndSlotStartsAtBefore(
        @Param("status") status: BookingStatus,
        @Param("before") before: Instant,
    ): List<BookingEntity>
}

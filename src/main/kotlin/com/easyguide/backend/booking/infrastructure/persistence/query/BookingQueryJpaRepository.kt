package com.easyguide.backend.booking.infrastructure.persistence.query

import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.infrastructure.persistence.entity.BookingEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.UUID

private const val SELECT_ROW = """
    SELECT new com.easyguide.backend.booking.infrastructure.persistence.query.BookingRow(
        b.id, t.id, t.title, t.city, s.startsAt, t.timezone, b.seats, b.totalPrice, b.status, b.comment,
        b.cancelReason, b.cancelledBy, b.createdAt, b.contactPhone,
        g.id, g.name, g.avatarUrl, g.phone, g.email,
        u.id, u.name, u.avatarUrl
    )
    FROM BookingEntity b
    JOIN TourSlotEntity s ON s.id = b.slotId
    JOIN TourEntity t ON t.id = s.tourId
    JOIN UserEntity g ON g.id = t.guideId
    JOIN UserEntity u ON u.id = b.userId
"""

interface BookingQueryJpaRepository : Repository<BookingEntity, UUID> {

    @Query(
        value = "$SELECT_ROW WHERE u.id = :userId AND (:status IS NULL OR b.status = :status) ORDER BY s.startsAt ASC",
        countQuery = "SELECT COUNT(b) FROM BookingEntity b " +
            "WHERE b.userId = :userId AND (:status IS NULL OR b.status = :status)",
    )
    fun findByTraveler(
        @Param("userId") userId: UUID,
        @Param("status") status: BookingStatus?,
        pageable: Pageable,
    ): Page<BookingRow>

    @Query(
        value = "$SELECT_ROW WHERE g.id = :guideId AND (:status IS NULL OR b.status = :status) " +
            "ORDER BY CASE WHEN b.status = com.easyguide.backend.booking.domain.model.BookingStatus.PENDING " +
            "THEN 0 ELSE 1 END, s.startsAt ASC",
        countQuery = "SELECT COUNT(b) FROM BookingEntity b " +
            "JOIN TourSlotEntity s ON s.id = b.slotId " +
            "JOIN TourEntity t ON t.id = s.tourId " +
            "WHERE t.guideId = :guideId AND (:status IS NULL OR b.status = :status)",
    )
    fun findByGuide(
        @Param("guideId") guideId: UUID,
        @Param("status") status: BookingStatus?,
        pageable: Pageable,
    ): Page<BookingRow>

    @Query("$SELECT_ROW WHERE b.id = :bookingId")
    fun findDetailsRow(@Param("bookingId") bookingId: UUID): BookingRow?
}

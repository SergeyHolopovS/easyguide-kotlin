package com.easyguide.backend.tourslot.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tour_slots")
class TourSlotEntity(
    @Id
    val id: UUID,

    @Column(name = "tour_id", nullable = false)
    var tourId: UUID,

    @Column(name = "starts_at", nullable = false)
    var startsAt: Instant,

    @Column(nullable = false)
    var capacity: Int,

    @Column(name = "booked_seats", nullable = false)
    var bookedSeats: Int,

    @Column(name = "is_cancelled", nullable = false)
    var isCancelled: Boolean,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
)

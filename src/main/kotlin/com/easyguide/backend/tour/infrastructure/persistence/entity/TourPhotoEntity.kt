package com.easyguide.backend.tour.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tour_photos")
class TourPhotoEntity(
    @Id
    val id: UUID,

    @Column(name = "tour_id", nullable = false)
    var tourId: UUID,

    @Column(nullable = false)
    var url: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
)

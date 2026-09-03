package com.easyguide.backend.review.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "reviews")
class ReviewEntity(
    @Id
    val id: UUID,

    @Column(name = "booking_id", nullable = false, unique = true)
    val bookingId: UUID,

    @Column(name = "tour_id", nullable = false)
    val tourId: UUID,

    @Column(name = "author_id", nullable = false)
    val authorId: UUID,

    @Column(nullable = false)
    val rating: Int,

    @Column(columnDefinition = "text")
    val text: String?,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
)

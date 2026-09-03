package com.easyguide.backend.tour.infrastructure.persistence.entity

import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tours")
class TourEntity(
    @Id
    val id: UUID,

    @Column(name = "guide_id", nullable = false)
    var guideId: UUID,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "text", nullable = false)
    var description: String,

    @Column(nullable = false)
    var city: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: TourCategory,

    @Column(name = "meeting_point", nullable = false)
    var meetingPoint: String,

    @Column(nullable = false)
    var timezone: String,

    @Column(name = "duration_minutes", nullable = false)
    var durationMinutes: Int,

    var price: BigDecimal?,

    @Column(name = "max_people", nullable = false)
    var maxPeople: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TourStatus,

    var rating: Double?,

    @Column(name = "reviews_count", nullable = false)
    var reviewsCount: Int,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
)

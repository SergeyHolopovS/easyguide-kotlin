package com.easyguide.backend.tour.infrastructure.persistence.query

import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import java.math.BigDecimal
import java.util.UUID

class TourDetailsRow(
    val id: UUID,
    val guideId: UUID,
    val title: String,
    val description: String,
    val city: String,
    val category: TourCategory,
    val meetingPoint: String,
    val timezone: String,
    val durationMinutes: Int,
    val price: BigDecimal?,
    val maxPeople: Int,
    val status: TourStatus,
    val rating: Double?,
    val reviewsCount: Int,
    val guideName: String,
    val guideAvatarUrl: String?,
    val guideBio: String?,
)

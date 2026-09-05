package com.easyguide.backend.tour.application.query

import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

data class TourPhotoView(
    val id: UUID,
    val url: String,
    val sortOrder: Int,
)

data class TourGuideView(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
)

data class TourDetails(
    val id: UUID,
    val guideId: UUID,
    val title: String,
    val description: String,
    val city: String,
    val category: TourCategory,
    val meetingPoint: String,
    val timezone: ZoneId,
    val durationMinutes: Int,
    val price: BigDecimal?,
    val maxPeople: Int,
    val status: TourStatus,
    val rating: Double?,
    val reviewsCount: Int,
    val photos: List<TourPhotoView>,
    val guide: TourGuideView,
)

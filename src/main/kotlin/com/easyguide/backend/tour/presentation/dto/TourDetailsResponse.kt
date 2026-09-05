package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.query.TourDetails
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

data class TourDetailsPhotoResponse(
    val id: UUID,
    val url: String,
    val sortOrder: Int,
)

data class TourGuideResponse(
    val id: UUID,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
)

data class TourDetailsResponse(
    val id: UUID,
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
    val photos: List<TourDetailsPhotoResponse>,
    val guide: TourGuideResponse,
)

fun TourDetails.toResponse(): TourDetailsResponse = TourDetailsResponse(
    id = id,
    title = title,
    description = description,
    city = city,
    category = category,
    meetingPoint = meetingPoint,
    timezone = timezone,
    durationMinutes = durationMinutes,
    price = price,
    maxPeople = maxPeople,
    status = status,
    rating = rating,
    reviewsCount = reviewsCount,
    photos = photos.map { TourDetailsPhotoResponse(id = it.id, url = it.url, sortOrder = it.sortOrder) },
    guide = TourGuideResponse(id = guide.id, name = guide.name, avatarUrl = guide.avatarUrl, bio = guide.bio),
)

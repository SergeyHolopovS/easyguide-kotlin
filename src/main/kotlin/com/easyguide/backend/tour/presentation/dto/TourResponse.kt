package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

data class TourPhotoResponse(
    val id: UUID,
    val url: String,
    val sortOrder: Int,
)

data class TourResponse(
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
    val photos: List<TourPhotoResponse>,
    val rating: Double?,
    val reviewsCount: Int,
)

fun TourResult.toResponse(): TourResponse = TourResponse(
    id = id,
    guideId = guideId,
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
    photos = photos.map { TourPhotoResponse(id = it.id, url = it.url, sortOrder = it.sortOrder) },
    rating = rating,
    reviewsCount = reviewsCount,
)

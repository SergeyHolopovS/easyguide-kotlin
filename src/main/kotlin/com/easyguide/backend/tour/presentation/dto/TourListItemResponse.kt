package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.query.TourListItem
import com.easyguide.backend.tour.domain.model.TourCategory
import java.math.BigDecimal
import java.util.UUID

data class TourListItemResponse(
    val id: UUID,
    val title: String,
    val city: String,
    val category: TourCategory,
    val price: BigDecimal?,
    val durationMinutes: Int,
    val rating: Double?,
    val reviewsCount: Int,
)

fun TourListItem.toResponse(): TourListItemResponse = TourListItemResponse(
    id = id,
    title = title,
    city = city,
    category = category,
    price = price,
    durationMinutes = durationMinutes,
    rating = rating,
    reviewsCount = reviewsCount,
)

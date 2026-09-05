package com.easyguide.backend.tour.application.query

import com.easyguide.backend.tour.domain.model.TourCategory
import java.math.BigDecimal
import java.util.UUID

data class TourListItem(
    val id: UUID,
    val title: String,
    val city: String,
    val category: TourCategory,
    val price: BigDecimal?,
    val durationMinutes: Int,
    val rating: Double?,
    val reviewsCount: Int,
)

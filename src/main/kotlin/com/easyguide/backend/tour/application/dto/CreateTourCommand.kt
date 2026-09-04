package com.easyguide.backend.tour.application.dto

import com.easyguide.backend.tour.domain.model.TourCategory
import java.math.BigDecimal
import java.time.ZoneId

data class CreateTourCommand(
    val title: String,
    val description: String,
    val city: String,
    val category: TourCategory,
    val meetingPoint: String,
    val timezone: ZoneId,
    val durationMinutes: Int,
    val price: BigDecimal?,
    val maxPeople: Int,
)

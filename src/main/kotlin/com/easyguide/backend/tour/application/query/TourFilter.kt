package com.easyguide.backend.tour.application.query

import com.easyguide.backend.tour.domain.model.TourCategory
import java.math.BigDecimal
import java.time.LocalDate

data class TourFilter(
    val city: String? = null,
    val category: TourCategory? = null,
    val priceMin: BigDecimal? = null,
    val priceMax: BigDecimal? = null,
    val date: LocalDate? = null,
    val q: String? = null,
)

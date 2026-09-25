package com.easyguide.backend.tour.presentation.dto

import com.easyguide.backend.tour.application.query.TourListItem
import com.easyguide.backend.tour.domain.model.TourCategory
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "Тур в списке (каталог, профиль гида)")
data class TourListItemResponse(
    @field:Schema(description = "ID тура")
    val id: UUID,
    @field:Schema(description = "Название")
    val title: String,
    @field:Schema(description = "Город", example = "Москва")
    val city: String,
    @field:Schema(description = "Категория")
    val category: TourCategory,
    @field:Schema(description = "Цена за одно место; `null`, если не задана", example = "2500")
    val price: BigDecimal?,
    @field:Schema(description = "Длительность, минут", example = "120")
    val durationMinutes: Int,
    @field:Schema(description = "Средний рейтинг 0–5; `null`, если отзывов нет", example = "4.7")
    val rating: Double?,
    @field:Schema(description = "Число отзывов")
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

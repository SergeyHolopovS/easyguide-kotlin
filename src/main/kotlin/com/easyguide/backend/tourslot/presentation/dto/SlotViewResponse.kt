package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.query.SlotView
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Schema(description = "Слот в календаре тура")
data class SlotViewResponse(
    @field:Schema(description = "ID слота")
    val id: UUID,
    @field:Schema(description = "Начало (UTC)")
    val startsAt: Instant,
    @field:Schema(description = "Дата начала по местному времени тура")
    val localDate: LocalDate,
    @field:Schema(description = "Время начала по местному времени тура")
    val localTime: LocalTime,
    @field:Schema(description = "Вместимость")
    val capacity: Int,
    @field:Schema(description = "Свободно мест")
    val availableSeats: Int,
    @field:Schema(description = "Можно ли сейчас забронировать (не отменён, не начался, есть места)")
    val bookable: Boolean,
)

fun SlotView.toResponse(): SlotViewResponse = SlotViewResponse(
    id = id,
    startsAt = startsAt,
    localDate = localDate,
    localTime = localTime,
    capacity = capacity,
    availableSeats = availableSeats,
    bookable = bookable,
)

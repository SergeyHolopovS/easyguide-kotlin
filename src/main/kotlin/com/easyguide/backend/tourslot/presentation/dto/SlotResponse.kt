package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.dto.SlotResult
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Слот расписания")
data class SlotResponse(
    @field:Schema(description = "ID слота")
    val id: UUID,
    @field:Schema(description = "ID тура")
    val tourId: UUID,
    @field:Schema(description = "Начало (UTC)")
    val startsAt: Instant,
    @field:Schema(description = "Вместимость")
    val capacity: Int,
    @field:Schema(description = "Занято мест")
    val bookedSeats: Int,
    @field:Schema(description = "Свободно мест")
    val availableSeats: Int,
    @field:Schema(description = "Отменён ли слот гидом")
    val isCancelled: Boolean,
)

fun SlotResult.toResponse(): SlotResponse = SlotResponse(
    id = id,
    tourId = tourId,
    startsAt = startsAt,
    capacity = capacity,
    bookedSeats = bookedSeats,
    availableSeats = availableSeats,
    isCancelled = isCancelled,
)

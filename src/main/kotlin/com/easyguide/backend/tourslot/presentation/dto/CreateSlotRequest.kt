package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.dto.CreateSlotCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Schema(description = "Данные нового слота")
data class CreateSlotRequest(
    @field:NotNull(message = "Время начала обязательно")
    @field:Schema(description = "Начало в UTC, строго в будущем", example = "2026-10-01T07:00:00Z")
    val startsAt: Instant,

    @field:Min(1, message = "Вместимость должна быть не меньше 1")
    @field:Schema(description = "Вместимость слота, минимум 1", example = "10")
    val capacity: Int,
)

fun CreateSlotRequest.toCommand(): CreateSlotCommand = CreateSlotCommand(
    startsAt = startsAt,
    capacity = capacity,
)

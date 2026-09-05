package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.dto.CreateSlotCommand
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class CreateSlotRequest(
    @field:NotNull(message = "Время начала обязательно")
    val startsAt: Instant,

    @field:Min(1, message = "Вместимость должна быть не меньше 1")
    val capacity: Int,
)

fun CreateSlotRequest.toCommand(): CreateSlotCommand = CreateSlotCommand(
    startsAt = startsAt,
    capacity = capacity,
)

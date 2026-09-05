package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.dto.CreateSlotsBulkCommand
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate
import java.time.LocalTime

data class CreateSlotsBulkRequest(
    @field:NotEmpty(message = "Список дат не должен быть пустым")
    val dates: List<LocalDate>,

    @field:NotEmpty(message = "Список времени не должен быть пустым")
    val times: List<LocalTime>,

    @field:Min(1, message = "Вместимость должна быть не меньше 1")
    val capacity: Int,
)

fun CreateSlotsBulkRequest.toCommand(): CreateSlotsBulkCommand = CreateSlotsBulkCommand(
    dates = dates,
    times = times,
    capacity = capacity,
)

data class CreateSlotsBulkResponse(
    val created: Int,
    val skipped: Int,
)

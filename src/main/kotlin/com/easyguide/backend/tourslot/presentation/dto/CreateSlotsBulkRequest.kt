package com.easyguide.backend.tourslot.presentation.dto

import com.easyguide.backend.tourslot.application.dto.CreateSlotsBulkCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "Массовое создание слотов: создаются все комбинации «дата × время»")
data class CreateSlotsBulkRequest(
    @field:NotEmpty(message = "Список дат не должен быть пустым")
    @field:Schema(description = "Даты слотов", example = "[\"2026-10-01\", \"2026-10-02\"]")
    val dates: List<LocalDate>,

    @field:NotEmpty(message = "Список времени не должен быть пустым")
    @field:Schema(description = "Местное время начала (в часовом поясе тура)", example = "[\"10:00:00\", \"15:00:00\"]")
    val times: List<LocalTime>,

    @field:Min(1, message = "Вместимость должна быть не меньше 1")
    @field:Schema(description = "Вместимость каждого слота, минимум 1", example = "10")
    val capacity: Int,
)

fun CreateSlotsBulkRequest.toCommand(): CreateSlotsBulkCommand = CreateSlotsBulkCommand(
    dates = dates,
    times = times,
    capacity = capacity,
)

@Schema(description = "Итог массового создания слотов")
data class CreateSlotsBulkResponse(
    @field:Schema(description = "Сколько слотов создано")
    val created: Int,
    @field:Schema(description = "Сколько пропущено, т.к. слот на это время уже существует")
    val skipped: Int,
)

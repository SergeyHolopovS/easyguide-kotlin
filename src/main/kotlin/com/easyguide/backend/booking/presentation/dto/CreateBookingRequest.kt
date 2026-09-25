package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.dto.CreateBookingCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

@Schema(description = "Данные новой брони")
data class CreateBookingRequest(
    @field:NotNull(message = "Слот обязателен")
    @field:Schema(description = "ID слота")
    val slotId: UUID,

    @field:Min(1, message = "Число мест должно быть не меньше 1")
    @field:Schema(description = "Число мест, минимум 1", example = "2")
    val seats: Int,

    @field:NotBlank(message = "Телефон не должен быть пустым")
    @field:Schema(description = "Контактный телефон для связи гида с туристом", example = "+79001234567")
    val contactPhone: String,

    @field:Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
    @field:Schema(description = "Комментарий гиду, до 1000 символов", example = "Придём с ребёнком")
    val comment: String?,
)

fun CreateBookingRequest.toCommand(): CreateBookingCommand = CreateBookingCommand(
    slotId = slotId,
    seats = seats,
    contactPhone = contactPhone,
    comment = comment,
)

package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.dto.CreateBookingCommand
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateBookingRequest(
    @field:NotNull(message = "Слот обязателен")
    val slotId: UUID,

    @field:Min(1, message = "Число мест должно быть не меньше 1")
    val seats: Int,

    @field:NotBlank(message = "Телефон не должен быть пустым")
    val contactPhone: String,

    @field:Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
    val comment: String?,
)

fun CreateBookingRequest.toCommand(): CreateBookingCommand = CreateBookingCommand(
    slotId = slotId,
    seats = seats,
    contactPhone = contactPhone,
    comment = comment,
)

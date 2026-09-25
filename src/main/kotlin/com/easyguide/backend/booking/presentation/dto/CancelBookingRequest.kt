package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.dto.CancelBookingCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Причина отмены брони")
data class CancelBookingRequest(
    @field:NotBlank(message = "Причина отмены не должна быть пустой")
    @field:Schema(description = "Причина отмены, не пустая", example = "Изменились планы")
    val reason: String,
)

fun CancelBookingRequest.toCommand(): CancelBookingCommand = CancelBookingCommand(reason = reason)

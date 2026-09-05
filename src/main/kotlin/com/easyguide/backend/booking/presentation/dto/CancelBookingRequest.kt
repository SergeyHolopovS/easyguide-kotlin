package com.easyguide.backend.booking.presentation.dto

import com.easyguide.backend.booking.application.dto.CancelBookingCommand
import jakarta.validation.constraints.NotBlank

data class CancelBookingRequest(
    @field:NotBlank(message = "Причина отмены не должна быть пустой")
    val reason: String,
)

fun CancelBookingRequest.toCommand(): CancelBookingCommand = CancelBookingCommand(reason = reason)

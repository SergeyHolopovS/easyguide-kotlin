package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.RefreshTokenCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Refresh-токен")
data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh-токен не должен быть пустым")
    @field:Schema(description = "Refresh-токен, полученный при входе/регистрации/предыдущем обновлении")
    val refreshToken: String,
)

fun RefreshTokenRequest.toCommand(): RefreshTokenCommand = RefreshTokenCommand(
    refreshToken = refreshToken,
)

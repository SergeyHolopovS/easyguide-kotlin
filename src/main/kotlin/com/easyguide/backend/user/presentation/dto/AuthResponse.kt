package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.AuthResult
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Результат регистрации/входа")
data class AuthResponse(
    @field:Schema(description = "JWT для заголовка `Authorization: Bearer <token>`")
    val token: String,
    @field:Schema(description = "Профиль пользователя")
    val user: UserResponse,
)

fun AuthResult.toResponse(): AuthResponse = AuthResponse(
    token = token,
    user = user.toResponse(),
)

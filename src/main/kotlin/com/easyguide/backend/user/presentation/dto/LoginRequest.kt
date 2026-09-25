package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.LoginCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Данные для входа")
data class LoginRequest(
    @field:NotBlank(message = "Почта не должна быть пустой")
    @field:Email(message = "Почта должна быть валидной")
    @field:Schema(description = "Email", example = "anna@example.com")
    val email: String,

    @field:NotBlank(message = "Пароль не должен быть пустым")
    @field:Schema(description = "Пароль", example = "Passw0rd!2026")
    val password: String,
)

fun LoginRequest.toCommand(): LoginCommand = LoginCommand(
    email = email,
    password = password,
)

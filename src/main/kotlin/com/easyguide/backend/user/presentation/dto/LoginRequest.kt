package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.LoginCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Почта не должна быть пустой")
    @field:Email(message = "Почта должна быть валидной")
    val email: String,

    @field:NotBlank(message = "Пароль не должен быть пустым")
    val password: String,
)

fun LoginRequest.toCommand(): LoginCommand = LoginCommand(
    email = email,
    password = password,
)

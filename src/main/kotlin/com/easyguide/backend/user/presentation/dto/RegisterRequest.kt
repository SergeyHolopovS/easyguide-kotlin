package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.RegisterUserCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Имя не должно быть пустым")
    val name: String,

    @field:NotBlank(message = "Почта не должна быть пустой")
    @field:Email(message = "Почта должна быть валидной")
    val email: String,

    @field:NotBlank(message = "Пароль не должен быть пустым")
    @field:Size(min = 8, message = "Пароль должен быть не короче 8 символов")
    val password: String,

    val phone: String? = null,
)

fun RegisterRequest.toCommand(): RegisterUserCommand = RegisterUserCommand(
    name = name,
    email = email,
    password = password,
    phone = phone,
)

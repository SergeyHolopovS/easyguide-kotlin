package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.RegisterUserCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Данные для регистрации")
data class RegisterRequest(
    @field:NotBlank(message = "Имя не должно быть пустым")
    @field:Schema(description = "Имя пользователя", example = "Анна Иванова")
    val name: String,

    @field:NotBlank(message = "Почта не должна быть пустой")
    @field:Email(message = "Почта должна быть валидной")
    @field:Schema(description = "Email — уникальный, используется как логин", example = "anna@example.com")
    val email: String,

    @field:NotBlank(message = "Пароль не должен быть пустым")
    @field:Size(min = 8, message = "Пароль должен быть не короче 8 символов")
    @field:Schema(description = "Пароль, минимум 8 символов", example = "Passw0rd!2026")
    val password: String,

    @field:Schema(description = "Телефон (необязательно)", example = "+79001234567")
    val phone: String? = null,
)

fun RegisterRequest.toCommand(): RegisterUserCommand = RegisterUserCommand(
    name = name,
    email = email,
    password = password,
    phone = phone,
)

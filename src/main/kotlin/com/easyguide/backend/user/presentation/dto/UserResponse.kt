package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.UserResult
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Профиль пользователя")
data class UserResponse(
    @field:Schema(description = "ID пользователя")
    val id: UUID,
    @field:Schema(description = "Имя", example = "Анна Иванова")
    val name: String,
    @field:Schema(description = "Email", example = "anna@example.com")
    val email: String,
    @field:Schema(description = "Телефон", example = "+79001234567")
    val phone: String?,
    @field:Schema(description = "URL аватара")
    val avatarUrl: String?,
    @field:Schema(description = "Включён ли режим гида", example = "false")
    val isGuide: Boolean,
    @field:Schema(description = "О себе")
    val bio: String?,
    @field:Schema(description = "Город", example = "Москва")
    val city: String?,
    @field:Schema(description = "Языки", example = "[\"ru\", \"en\"]")
    val languages: List<String>,
    @field:Schema(description = "Дата регистрации (UTC)")
    val createdAt: Instant,
)

fun UserResult.toResponse(): UserResponse = UserResponse(
    id = id,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    isGuide = isGuide,
    bio = bio,
    city = city,
    languages = languages,
    createdAt = createdAt,
)

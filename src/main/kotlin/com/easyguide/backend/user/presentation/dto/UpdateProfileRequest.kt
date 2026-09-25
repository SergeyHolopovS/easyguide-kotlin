package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.UpdateProfileCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "Частичное обновление профиля: `null`/отсутствующие поля не меняются")
data class UpdateProfileRequest(
    @field:Size(min = 1, message = "Имя не должно быть пустым")
    @field:Schema(description = "Имя, не пустое", example = "Анна Иванова")
    val name: String? = null,

    @field:Schema(description = "Телефон", example = "+79001234567")
    val phone: String? = null,

    @field:Schema(description = "URL аватара (см. `POST /api/files`)", example = "http://localhost:8080/uploads/avatar.png")
    val avatarUrl: String? = null,

    @field:Schema(description = "О себе; обязательно для гида", example = "Профессиональный гид, 10 лет показываю Москву")
    val bio: String? = null,

    @field:Schema(description = "Город; обязателен для гида", example = "Москва")
    val city: String? = null,

    @field:Schema(description = "Языки; для гида — минимум один", example = "[\"ru\", \"en\"]")
    val languages: List<String>? = null,
)

fun UpdateProfileRequest.toCommand(): UpdateProfileCommand = UpdateProfileCommand(
    name = name,
    phone = phone,
    avatarUrl = avatarUrl,
    bio = bio,
    city = city,
    languages = languages,
)

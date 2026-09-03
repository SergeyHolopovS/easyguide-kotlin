package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.UpdateProfileCommand
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:Size(min = 1, message = "Имя не должно быть пустым")
    val name: String? = null,

    val phone: String? = null,

    val avatarUrl: String? = null,

    val bio: String? = null,

    val city: String? = null,

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

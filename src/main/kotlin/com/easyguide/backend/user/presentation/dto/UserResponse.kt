package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.UserResult
import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val isGuide: Boolean,
    val bio: String?,
    val city: String?,
    val languages: List<String>,
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

package com.easyguide.backend.user.application.dto

import com.easyguide.backend.user.domain.model.User
import java.time.Instant
import java.util.UUID

data class UserResult(
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

fun User.toResult(): UserResult = UserResult(
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

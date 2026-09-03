package com.easyguide.backend.user.application.dto

data class UpdateProfileCommand(
    val name: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val city: String? = null,
    val languages: List<String>? = null,
)

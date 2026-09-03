package com.easyguide.backend.user.presentation.dto

import com.easyguide.backend.user.application.dto.AuthResult

data class AuthResponse(
    val token: String,
    val user: UserResponse,
)

fun AuthResult.toResponse(): AuthResponse = AuthResponse(
    token = token,
    user = user.toResponse(),
)

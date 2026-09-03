package com.easyguide.backend.user.application.dto

data class AuthResult(
    val token: String,
    val user: UserResult,
)

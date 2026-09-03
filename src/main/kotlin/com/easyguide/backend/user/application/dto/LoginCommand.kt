package com.easyguide.backend.user.application.dto

data class LoginCommand(
    val email: String,
    val password: String,
)

package com.easyguide.backend.user.application.dto

data class RegisterUserCommand(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
)

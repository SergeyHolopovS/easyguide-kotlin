package com.easyguide.backend.shared.application.port

interface PasswordHasher {
    fun hash(rawPassword: String): String
    fun matches(rawPassword: String, hashedPassword: String): Boolean
}

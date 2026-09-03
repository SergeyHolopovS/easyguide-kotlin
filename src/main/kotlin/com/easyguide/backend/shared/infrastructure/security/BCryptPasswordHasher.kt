package com.easyguide.backend.shared.infrastructure.security

import com.easyguide.backend.shared.application.port.PasswordHasher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordHasher(
    private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) : PasswordHasher {

    override fun hash(rawPassword: String): String =
        encoder.encode(rawPassword)!!

    override fun matches(rawPassword: String, hashedPassword: String): Boolean =
        encoder.matches(rawPassword, hashedPassword)
}

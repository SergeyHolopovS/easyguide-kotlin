package com.easyguide.backend.shared.infrastructure.security

import com.easyguide.backend.shared.application.port.RefreshTokenGenerator
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

private const val TOKEN_BYTES = 32

@Component
class SecureRandomRefreshTokenGenerator : RefreshTokenGenerator {

    private val random = SecureRandom()

    override fun generate(): String {
        val bytes = ByteArray(TOKEN_BYTES)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    // токен имеет 256 бит энтропии, поэтому соль/медленный хеш не нужны — достаточно SHA-256
    override fun hash(token: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(token.toByteArray())
            .joinToString("") { "%02x".format(it) }
}

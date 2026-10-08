package com.easyguide.backend.user.domain.model

import java.time.Instant
import java.util.UUID

class RefreshToken(
    val id: UUID,
    val userId: UUID,
    val tokenHash: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    revokedAt: Instant? = null,
) {

    var revokedAt: Instant? = revokedAt
        private set

    init {
        require(tokenHash.isNotBlank()) { "Хеш токена не должен быть пустым" }
        require(expiresAt.isAfter(createdAt)) { "Срок действия токена должен быть позже момента создания" }
    }

    val isRevoked: Boolean
        get() = revokedAt != null

    fun isExpired(now: Instant): Boolean = !now.isBefore(expiresAt)
}

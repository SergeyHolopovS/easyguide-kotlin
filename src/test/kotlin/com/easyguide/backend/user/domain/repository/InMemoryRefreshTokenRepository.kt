package com.easyguide.backend.user.domain.repository

import com.easyguide.backend.user.domain.model.RefreshToken
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryRefreshTokenRepository : RefreshTokenRepository {

    private val storage = ConcurrentHashMap<UUID, RefreshToken>()

    fun all(): List<RefreshToken> = storage.values.toList()

    override fun findByTokenHash(tokenHash: String): RefreshToken? =
        storage.values.find { it.tokenHash == tokenHash }

    override fun save(refreshToken: RefreshToken): RefreshToken {
        storage[refreshToken.id] = refreshToken
        return refreshToken
    }

    @Synchronized
    override fun tryRevoke(id: UUID, at: Instant): Boolean {
        val token = storage[id] ?: return false
        if (token.isRevoked) return false
        storage[id] = token.revokedCopy(at)
        return true
    }

    @Synchronized
    override fun revokeAllByUserId(userId: UUID, at: Instant) {
        storage.values
            .filter { it.userId == userId && !it.isRevoked }
            .forEach { storage[it.id] = it.revokedCopy(at) }
    }

    private fun RefreshToken.revokedCopy(at: Instant) = RefreshToken(
        id = id,
        userId = userId,
        tokenHash = tokenHash,
        expiresAt = expiresAt,
        createdAt = createdAt,
        revokedAt = at,
    )
}

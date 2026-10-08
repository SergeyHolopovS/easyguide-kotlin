package com.easyguide.backend.user.domain.repository

import com.easyguide.backend.user.domain.model.RefreshToken
import java.time.Instant
import java.util.UUID

interface RefreshTokenRepository {
    fun findByTokenHash(tokenHash: String): RefreshToken?
    fun save(refreshToken: RefreshToken): RefreshToken

    /** Атомарно отзывает токен, если он ещё не отозван. true — отозвал именно этот вызов. */
    fun tryRevoke(id: UUID, at: Instant): Boolean

    fun revokeAllByUserId(userId: UUID, at: Instant)
}

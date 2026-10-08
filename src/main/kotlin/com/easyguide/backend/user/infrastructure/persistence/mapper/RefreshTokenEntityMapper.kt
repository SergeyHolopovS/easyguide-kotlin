package com.easyguide.backend.user.infrastructure.persistence.mapper

import com.easyguide.backend.user.domain.model.RefreshToken
import com.easyguide.backend.user.infrastructure.persistence.entity.RefreshTokenEntity

fun RefreshToken.toEntity(): RefreshTokenEntity = RefreshTokenEntity(
    id = id,
    userId = userId,
    tokenHash = tokenHash,
    expiresAt = expiresAt,
    revokedAt = revokedAt,
    createdAt = createdAt,
)

fun RefreshTokenEntity.toDomain(): RefreshToken = RefreshToken(
    id = id,
    userId = userId,
    tokenHash = tokenHash,
    expiresAt = expiresAt,
    createdAt = createdAt,
    revokedAt = revokedAt,
)

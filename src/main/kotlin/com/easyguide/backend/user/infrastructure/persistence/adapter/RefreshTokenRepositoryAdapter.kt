package com.easyguide.backend.user.infrastructure.persistence.adapter

import com.easyguide.backend.user.domain.model.RefreshToken
import com.easyguide.backend.user.domain.repository.RefreshTokenRepository
import com.easyguide.backend.user.infrastructure.persistence.jpa.RefreshTokenJpaRepository
import com.easyguide.backend.user.infrastructure.persistence.mapper.toDomain
import com.easyguide.backend.user.infrastructure.persistence.mapper.toEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Repository
class RefreshTokenRepositoryAdapter(
    private val jpaRepository: RefreshTokenJpaRepository,
) : RefreshTokenRepository {

    override fun findByTokenHash(tokenHash: String): RefreshToken? =
        jpaRepository.findByTokenHash(tokenHash)?.toDomain()

    override fun save(refreshToken: RefreshToken): RefreshToken =
        jpaRepository.save(refreshToken.toEntity()).toDomain()

    @Transactional
    override fun tryRevoke(id: UUID, at: Instant): Boolean =
        jpaRepository.tryRevoke(id, at) == 1

    @Transactional
    override fun revokeAllByUserId(userId: UUID, at: Instant) {
        jpaRepository.revokeAllByUserId(userId, at)
    }
}

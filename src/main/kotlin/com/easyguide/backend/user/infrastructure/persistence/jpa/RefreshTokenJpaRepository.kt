package com.easyguide.backend.user.infrastructure.persistence.jpa

import com.easyguide.backend.user.infrastructure.persistence.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, UUID> {

    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?

    /** Отзывает токен, только если он ещё активен. Возвращает число изменённых строк (0 или 1). */
    @Modifying
    @Query(
        value = "UPDATE refresh_tokens SET revoked_at = :at WHERE id = :id AND revoked_at IS NULL",
        nativeQuery = true,
    )
    fun tryRevoke(@Param("id") id: UUID, @Param("at") at: Instant): Int

    @Modifying
    @Query(
        value = "UPDATE refresh_tokens SET revoked_at = :at WHERE user_id = :userId AND revoked_at IS NULL",
        nativeQuery = true,
    )
    fun revokeAllByUserId(@Param("userId") userId: UUID, @Param("at") at: Instant): Int
}

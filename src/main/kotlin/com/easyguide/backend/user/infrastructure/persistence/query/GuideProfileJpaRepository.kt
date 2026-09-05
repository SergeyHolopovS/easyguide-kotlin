package com.easyguide.backend.user.infrastructure.persistence.query

import com.easyguide.backend.user.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface GuideProfileJpaRepository : Repository<UserEntity, UUID> {

    @Query(
        "SELECT new com.easyguide.backend.user.infrastructure.persistence.query.GuideProfileRow(" +
            "u.id, u.name, u.avatarUrl, u.bio, u.city, u.languages, u.createdAt) " +
            "FROM UserEntity u " +
            "WHERE u.id = :userId AND u.isGuide = true"
    )
    fun findGuideRow(@Param("userId") userId: UUID): GuideProfileRow?

    @Query(
        "SELECT new com.easyguide.backend.user.infrastructure.persistence.query.GuideRatingAggregateRow(" +
            "AVG(r.rating), COUNT(r)) " +
            "FROM ReviewEntity r JOIN TourEntity t ON t.id = r.tourId " +
            "WHERE t.guideId = :userId"
    )
    fun findRatingAggregate(@Param("userId") userId: UUID): GuideRatingAggregateRow
}

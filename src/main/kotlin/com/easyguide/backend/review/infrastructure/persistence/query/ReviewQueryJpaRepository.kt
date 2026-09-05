package com.easyguide.backend.review.infrastructure.persistence.query

import com.easyguide.backend.review.application.query.ReviewListItem
import com.easyguide.backend.review.infrastructure.persistence.entity.ReviewEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ReviewQueryJpaRepository : Repository<ReviewEntity, UUID> {

    @Query(
        value = "SELECT new com.easyguide.backend.review.application.query.ReviewListItem(" +
            "r.id, r.rating, r.text, u.name, u.avatarUrl, r.createdAt) " +
            "FROM ReviewEntity r JOIN UserEntity u ON u.id = r.authorId " +
            "WHERE r.tourId = :tourId " +
            "ORDER BY r.createdAt DESC",
        countQuery = "SELECT COUNT(r) FROM ReviewEntity r WHERE r.tourId = :tourId",
    )
    fun findByTour(@Param("tourId") tourId: UUID, pageable: Pageable): Page<ReviewListItem>
}

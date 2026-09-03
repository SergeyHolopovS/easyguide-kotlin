package com.easyguide.backend.review.infrastructure.persistence.jpa

import com.easyguide.backend.review.infrastructure.persistence.entity.ReviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ReviewJpaRepository : JpaRepository<ReviewEntity, UUID> {

    fun findByBookingId(bookingId: UUID): ReviewEntity?

    @Query("SELECT r.rating FROM ReviewEntity r WHERE r.tourId = :tourId")
    fun findRatingsByTourId(@Param("tourId") tourId: UUID): List<Int>
}

package com.easyguide.backend.tour.infrastructure.persistence.query

import com.easyguide.backend.tour.application.query.TourListItem
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.infrastructure.persistence.entity.TourEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

private const val SEARCH_WHERE = """
    t.status = com.easyguide.backend.tour.domain.model.TourStatus.PUBLISHED
    AND (:city IS NULL OR t.city = :city)
    AND (:category IS NULL OR t.category = :category)
    AND (:priceMin IS NULL OR t.price >= :priceMin)
    AND (:priceMax IS NULL OR t.price <= :priceMax)
    AND (:q IS NULL OR lower(t.title) LIKE lower(concat('%', :q, '%')))
    AND (
        :date IS NULL
        OR EXISTS (
            SELECT 1 FROM TourSlotEntity s
            WHERE s.tourId = t.id
              AND s.isCancelled = false
              AND s.bookedSeats < s.capacity
              AND CAST(s.startsAt AS date) = :date
        )
    )
"""

interface TourCatalogJpaRepository : Repository<TourEntity, UUID> {

    @Query(
        value = "SELECT new com.easyguide.backend.tour.application.query.TourListItem(" +
            "t.id, t.title, t.city, t.category, t.price, t.durationMinutes, t.rating, t.reviewsCount) " +
            "FROM TourEntity t WHERE $SEARCH_WHERE",
        countQuery = "SELECT COUNT(t) FROM TourEntity t WHERE $SEARCH_WHERE",
    )
    fun search(
        @Param("city") city: String?,
        @Param("category") category: TourCategory?,
        @Param("priceMin") priceMin: BigDecimal?,
        @Param("priceMax") priceMax: BigDecimal?,
        @Param("q") q: String?,
        @Param("date") date: LocalDate?,
        pageable: Pageable,
    ): Page<TourListItem>

    @Query(
        "SELECT new com.easyguide.backend.tour.infrastructure.persistence.query.TourDetailsRow(" +
            "t.id, t.guideId, t.title, t.description, t.city, t.category, t.meetingPoint, " +
            "t.timezone, t.durationMinutes, t.price, t.maxPeople, t.status, t.rating, t.reviewsCount, " +
            "u.name, u.avatarUrl, u.bio) " +
            "FROM TourEntity t JOIN UserEntity u ON u.id = t.guideId " +
            "WHERE t.id = :id"
    )
    fun findDetailsRow(@Param("id") id: UUID): TourDetailsRow?

    @Query(
        "SELECT new com.easyguide.backend.tour.application.query.TourListItem(" +
            "t.id, t.title, t.city, t.category, t.price, t.durationMinutes, t.rating, t.reviewsCount) " +
            "FROM TourEntity t " +
            "WHERE t.guideId = :guideId AND t.status = com.easyguide.backend.tour.domain.model.TourStatus.PUBLISHED " +
            "ORDER BY t.createdAt DESC"
    )
    fun findPublishedByGuideId(@Param("guideId") guideId: UUID): List<TourListItem>
}

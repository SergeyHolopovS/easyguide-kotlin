package com.easyguide.backend.tour.infrastructure.persistence.mapper

import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourPhoto
import com.easyguide.backend.tour.infrastructure.persistence.entity.TourEntity
import java.time.Instant
import java.time.ZoneId

fun Tour.toEntity(): TourEntity = TourEntity(
    id = id,
    guideId = guideId,
    title = title,
    description = description,
    city = city,
    category = category,
    meetingPoint = meetingPoint,
    timezone = timezone.id,
    durationMinutes = durationMinutes,
    price = price,
    maxPeople = maxPeople,
    status = status,
    rating = rating,
    reviewsCount = reviewsCount,
    createdAt = Instant.now(), // перезатирается Hibernate (@CreationTimestamp) при реальной вставке
)

fun TourEntity.toDomain(photos: List<TourPhoto>): Tour = Tour(
    id = id,
    guideId = guideId,
    title = title,
    description = description,
    city = city,
    category = category,
    meetingPoint = meetingPoint,
    timezone = ZoneId.of(timezone),
    durationMinutes = durationMinutes,
    price = price,
    maxPeople = maxPeople,
    status = status,
    photos = photos,
    rating = rating,
    reviewsCount = reviewsCount,
)

fun TourEntity.updateFrom(tour: Tour) {
    guideId = tour.guideId
    title = tour.title
    description = tour.description
    city = tour.city
    category = tour.category
    meetingPoint = tour.meetingPoint
    timezone = tour.timezone.id
    durationMinutes = tour.durationMinutes
    price = tour.price
    maxPeople = tour.maxPeople
    status = tour.status
    rating = tour.rating
    reviewsCount = tour.reviewsCount
}

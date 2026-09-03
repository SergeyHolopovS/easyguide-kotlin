package com.easyguide.backend.review.infrastructure.persistence.mapper

import com.easyguide.backend.review.domain.model.Review
import com.easyguide.backend.review.infrastructure.persistence.entity.ReviewEntity

fun Review.toEntity(): ReviewEntity = ReviewEntity(
    id = id,
    bookingId = bookingId,
    tourId = tourId,
    authorId = authorId,
    rating = rating,
    text = comment,
    createdAt = createdAt,
)

fun ReviewEntity.toDomain(): Review = Review(
    id = id,
    tourId = tourId,
    authorId = authorId,
    bookingId = bookingId,
    rating = rating,
    comment = text,
    createdAt = createdAt,
)

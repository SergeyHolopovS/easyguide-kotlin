package com.easyguide.backend.tour.infrastructure.persistence.mapper

import com.easyguide.backend.tour.domain.model.TourPhoto
import com.easyguide.backend.tour.infrastructure.persistence.entity.TourPhotoEntity
import java.time.Instant
import java.util.UUID

fun TourPhoto.toEntity(tourId: UUID): TourPhotoEntity = TourPhotoEntity(
    id = id,
    tourId = tourId,
    url = url,
    sortOrder = sortOrder,
    createdAt = Instant.now(), // перезатирается Hibernate (@CreationTimestamp) при реальной вставке
)

fun TourPhotoEntity.toDomain(): TourPhoto = TourPhoto(
    id = id,
    url = url,
    sortOrder = sortOrder,
)

package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.shared.exceptions.exception.AccessDeniedDomainException
import com.easyguide.backend.tour.domain.model.Tour
import java.util.UUID

internal fun requireTourOwner(tour: Tour, userId: UUID) {
    if (tour.guideId != userId) {
        throw AccessDeniedDomainException("Тур принадлежит другому гиду")
    }
}

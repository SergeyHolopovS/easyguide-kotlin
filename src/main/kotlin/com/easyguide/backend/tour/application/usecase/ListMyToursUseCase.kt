package com.easyguide.backend.tour.application.usecase

import com.easyguide.backend.tour.application.dto.TourResult
import com.easyguide.backend.tour.application.dto.toResult
import com.easyguide.backend.tour.domain.repository.TourRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListMyToursUseCase(
    private val tourRepository: TourRepository,
) {

    fun execute(guideId: UUID): List<TourResult> =
        tourRepository.findByGuideId(guideId).map { it.toResult() }
}

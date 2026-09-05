package com.easyguide.backend.user.application.usecase.guide

import com.easyguide.backend.user.application.query.GuideProfileQuery
import com.easyguide.backend.user.application.query.GuideProfileView
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetGuideProfileUseCase(
    private val guideProfileQuery: GuideProfileQuery,
) {

    fun execute(userId: UUID): GuideProfileView? = guideProfileQuery.findById(userId)
}

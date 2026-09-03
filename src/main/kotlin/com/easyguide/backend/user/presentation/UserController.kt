package com.easyguide.backend.user.presentation

import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.user.application.usecase.profile.BecomeGuideUseCase
import com.easyguide.backend.user.application.usecase.profile.UpdateProfileUseCase
import com.easyguide.backend.user.presentation.dto.UpdateProfileRequest
import com.easyguide.backend.user.presentation.dto.UserResponse
import com.easyguide.backend.user.presentation.dto.toCommand
import com.easyguide.backend.user.presentation.dto.toResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val becomeGuideUseCase: BecomeGuideUseCase,
) {

    @PatchMapping("/me")
    fun updateProfile(
        @CurrentUserId userId: UUID,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): UserResponse =
        updateProfileUseCase.execute(userId, request.toCommand()).toResponse()

    @PostMapping("/me/become-guide")
    fun becomeGuide(@CurrentUserId userId: UUID): UserResponse =
        becomeGuideUseCase.execute(userId).toResponse()
}

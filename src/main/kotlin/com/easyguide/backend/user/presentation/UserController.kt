package com.easyguide.backend.user.presentation

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.user.application.usecase.guide.GetGuideProfileUseCase
import com.easyguide.backend.user.application.usecase.profile.BecomeGuideUseCase
import com.easyguide.backend.user.application.usecase.profile.UpdateProfileUseCase
import com.easyguide.backend.user.presentation.dto.GuideProfileResponse
import com.easyguide.backend.user.presentation.dto.UpdateProfileRequest
import com.easyguide.backend.user.presentation.dto.UserResponse
import com.easyguide.backend.user.presentation.dto.toCommand
import com.easyguide.backend.user.presentation.dto.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = ApiTags.PROFILE)
@RestController
@RequestMapping("/api/users")
class UserController(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val becomeGuideUseCase: BecomeGuideUseCase,
    private val getGuideProfileUseCase: GetGuideProfileUseCase,
) {

    @Operation(
        summary = "Обновить свой профиль",
        description = "Частичное обновление: изменяются только переданные (не `null`) поля.",
    )
    @PatchMapping("/me")
    fun updateProfile(
        @CurrentUserId userId: UUID,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): UserResponse =
        updateProfileUseCase.execute(userId, request.toCommand()).toResponse()

    @Operation(
        summary = "Стать гидом",
        description = "Включает у текущего пользователя режим гида — после этого он может создавать туры. " +
            "Перед вызовом в профиле должны быть заполнены `bio`, `city` и хотя бы один язык в `languages` " +
            "(`PATCH /api/users/me`). Повторный вызов для гида ничего не меняет.",
    )
    @PostMapping("/me/become-guide")
    fun becomeGuide(@CurrentUserId userId: UUID): UserResponse =
        becomeGuideUseCase.execute(userId).toResponse()

    @Operation(
        summary = "Публичный профиль гида",
        description = "Доступен без авторизации: информация о гиде, средний рейтинг, число отзывов и список его туров.",
    )
    @ApiError(404, "Гид не найден (или пользователь с таким ID не является гидом)")
    @GetMapping("/{id}")
    fun guideProfile(@Parameter(description = "ID гида") @PathVariable id: UUID): GuideProfileResponse =
        (getGuideProfileUseCase.execute(id) ?: throw EntityNotFoundException("Гид", id)).toResponse()
}

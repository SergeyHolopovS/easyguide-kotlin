package com.easyguide.backend.user.presentation

import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.user.application.usecase.auth.GetCurrentUserUseCase
import com.easyguide.backend.user.application.usecase.auth.LoginUseCase
import com.easyguide.backend.user.application.usecase.auth.RegisterUserUseCase
import com.easyguide.backend.user.presentation.dto.AuthResponse
import com.easyguide.backend.user.presentation.dto.LoginRequest
import com.easyguide.backend.user.presentation.dto.RegisterRequest
import com.easyguide.backend.user.presentation.dto.UserResponse
import com.easyguide.backend.user.presentation.dto.toCommand
import com.easyguide.backend.user.presentation.dto.toResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse =
        registerUserUseCase.execute(request.toCommand()).toResponse()

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse =
        loginUseCase.execute(request.toCommand()).toResponse()

    @GetMapping("/me")
    fun me(@CurrentUserId userId: UUID): UserResponse =
        getCurrentUserUseCase.execute(userId).toResponse()
}

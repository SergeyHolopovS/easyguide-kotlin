package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.application.port.PasswordHasher
import com.easyguide.backend.shared.application.port.TokenIssuer
import com.easyguide.backend.user.application.dto.AuthResult
import com.easyguide.backend.user.application.dto.RegisterUserCommand
import com.easyguide.backend.user.application.dto.toResult
import com.easyguide.backend.user.domain.exception.EmailAlreadyTakenException
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenIssuer: TokenIssuer,
    private val clock: Clock,
) {

    fun execute(command: RegisterUserCommand): AuthResult {
        if (userRepository.existsByEmail(command.email)) {
            throw EmailAlreadyTakenException(command.email)
        }

        val user = User(
            id = UUID.randomUUID(),
            name = command.name,
            email = command.email,
            passwordHash = passwordHasher.hash(command.password),
            phone = command.phone,
            isGuide = false,
            avatarUrl = null,
            bio = null,
            city = null,
            languages = emptyList(),
            createdAt = clock.now(),
        )

        val saved = userRepository.save(user)
        val token = tokenIssuer.issue(saved.id)

        return AuthResult(token = token, user = saved.toResult())
    }
}

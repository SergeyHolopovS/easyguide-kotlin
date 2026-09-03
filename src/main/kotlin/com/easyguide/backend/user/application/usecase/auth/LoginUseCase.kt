package com.easyguide.backend.user.application.usecase.auth

import com.easyguide.backend.shared.application.port.PasswordHasher
import com.easyguide.backend.shared.application.port.TokenIssuer
import com.easyguide.backend.user.application.dto.AuthResult
import com.easyguide.backend.user.application.dto.LoginCommand
import com.easyguide.backend.user.application.dto.toResult
import com.easyguide.backend.user.domain.exception.InvalidCredentialsException
import com.easyguide.backend.user.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenIssuer: TokenIssuer,
) {

    fun execute(command: LoginCommand): AuthResult {
        val user = userRepository.findByEmail(command.email)
            ?: throw InvalidCredentialsException()

        if (!passwordHasher.matches(command.password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        val token = tokenIssuer.issue(user.id)

        return AuthResult(token = token, user = user.toResult())
    }
}

package com.easyguide.backend.user.application.usecase.profile

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.user.application.dto.UserResult
import com.easyguide.backend.user.application.dto.toResult
import com.easyguide.backend.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BecomeGuideUseCase(
    private val userRepository: UserRepository,
) {

    fun execute(userId: UUID): UserResult {
        val user = userRepository.findById(userId)
            ?: throw EntityNotFoundException("Пользователь", userId)

        user.becomeGuide()

        return userRepository.save(user).toResult()
    }
}

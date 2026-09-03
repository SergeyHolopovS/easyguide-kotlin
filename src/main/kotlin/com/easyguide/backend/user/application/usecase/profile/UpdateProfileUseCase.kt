package com.easyguide.backend.user.application.usecase.profile

import com.easyguide.backend.shared.exceptions.exception.EntityNotFoundException
import com.easyguide.backend.user.application.dto.UpdateProfileCommand
import com.easyguide.backend.user.application.dto.UserResult
import com.easyguide.backend.user.application.dto.toResult
import com.easyguide.backend.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateProfileUseCase(
    private val userRepository: UserRepository,
) {

    fun execute(userId: UUID, command: UpdateProfileCommand): UserResult {
        val user = userRepository.findById(userId)
            ?: throw EntityNotFoundException("Пользователь", userId)

        user.updateProfile(
            name = command.name ?: user.name,
            phone = command.phone ?: user.phone,
            avatarUrl = command.avatarUrl ?: user.avatarUrl,
            bio = command.bio ?: user.bio,
            city = command.city ?: user.city,
            languages = command.languages ?: user.languages,
        )

        return userRepository.save(user).toResult()
    }
}

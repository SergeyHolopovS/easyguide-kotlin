package com.easyguide.backend.user.infrastructure.persistence.adapter

import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.UserRepository
import com.easyguide.backend.user.infrastructure.persistence.jpa.UserJpaRepository
import com.easyguide.backend.user.infrastructure.persistence.mapper.toDomain
import com.easyguide.backend.user.infrastructure.persistence.mapper.toEntity
import com.easyguide.backend.user.infrastructure.persistence.mapper.updateFrom
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryAdapter(
    private val jpaRepository: UserJpaRepository,
) : UserRepository {

    override fun findById(id: UUID): User? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? =
        jpaRepository.findByEmail(email)?.toDomain()

    override fun existsByEmail(email: String): Boolean =
        jpaRepository.existsByEmail(email)

    override fun save(user: User): User {
        val existing = jpaRepository.findById(user.id).orElse(null)
        val entity = if (existing != null) {
            existing.updateFrom(user)
            existing
        } else {
            user.toEntity()
        }
        return jpaRepository.save(entity).toDomain()
    }
}

package com.easyguide.backend.user.domain.repository

import com.easyguide.backend.user.domain.model.User
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryUserRepository : UserRepository {

    private val storage = ConcurrentHashMap<UUID, User>()

    override fun findById(id: UUID): User? = storage[id]

    override fun findByEmail(email: String): User? =
        storage.values.find { it.email == email }

    override fun existsByEmail(email: String): Boolean =
        storage.values.any { it.email == email }

    override fun save(user: User): User {
        storage[user.id] = user
        return user
    }
}

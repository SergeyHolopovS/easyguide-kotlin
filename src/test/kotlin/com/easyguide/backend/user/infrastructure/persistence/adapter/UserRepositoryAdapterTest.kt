package com.easyguide.backend.user.infrastructure.persistence.adapter

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.infrastructure.persistence.jpa.UserJpaRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration::class)
class UserRepositoryAdapterTest {

    @Autowired
    private lateinit var jpaRepository: UserJpaRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private val adapter by lazy { UserRepositoryAdapter(jpaRepository) }

    private fun user(email: String = "user-${UUID.randomUUID()}@example.com"): User = User(
        id = UUID.randomUUID(),
        name = "Иван Иванов",
        email = email,
        passwordHash = "hash",
        phone = "+79990000000",
        isGuide = false,
        avatarUrl = "https://example.com/avatar.jpg",
        bio = "Гид по Питеру",
        city = "Санкт-Петербург",
        languages = listOf("ru", "en"),
        createdAt = Instant.now(),
    )

    @Test
    fun `save и findById возвращают те же поля`() {
        val user = user()

        adapter.save(user)
        entityManager.flush()
        entityManager.clear()

        val loaded = adapter.findById(user.id)

        assertEquals(user.id, loaded?.id)
        assertEquals(user.name, loaded?.name)
        assertEquals(user.email, loaded?.email)
        assertEquals(user.passwordHash, loaded?.passwordHash)
        assertEquals(user.phone, loaded?.phone)
        assertEquals(user.isGuide, loaded?.isGuide)
        assertEquals(user.avatarUrl, loaded?.avatarUrl)
        assertEquals(user.bio, loaded?.bio)
        assertEquals(user.city, loaded?.city)
        assertEquals(user.languages, loaded?.languages)
    }

    @Test
    fun `findByEmail и existsByEmail находят сохранённого пользователя`() {
        val user = user()
        adapter.save(user)
        entityManager.flush()
        entityManager.clear()

        assertEquals(user.id, adapter.findByEmail(user.email)?.id)
        assertTrue(adapter.existsByEmail(user.email))
        assertFalse(adapter.existsByEmail("nobody-${UUID.randomUUID()}@example.com"))
    }

    @Test
    fun `дубль email нарушает unique-констрейнт`() {
        val email = "dup-${UUID.randomUUID()}@example.com"
        adapter.save(user(email = email))
        entityManager.flush()

        assertFailsWith<DataIntegrityViolationException> {
            adapter.save(user(email = email))
            entityManager.flush()
        }
    }
}

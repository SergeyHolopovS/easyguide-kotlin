package com.easyguide.backend.user.domain.model

import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UserTest {

    private fun user(
        isGuide: Boolean = false,
        bio: String? = "Люблю показывать город гостям",
        city: String? = "Санкт-Петербург",
        languages: List<String> = listOf("ru", "en"),
    ): User = User(
        id = UUID.randomUUID(),
        name = "Иван Иванов",
        email = "ivan@example.com",
        passwordHash = "hash",
        phone = null,
        isGuide = isGuide,
        avatarUrl = null,
        bio = bio,
        city = city,
        languages = languages,
        createdAt = Instant.now(),
    )

    @Test
    fun `becomeGuide делает пользователя гидом при заполненном профиле`() {
        val user = user()

        user.becomeGuide()

        assertTrue(user.isGuide)
    }

    @Test
    fun `becomeGuide кидает исключение без описания`() {
        val user = user(bio = null)

        assertFailsWith<IllegalArgumentException> { user.becomeGuide() }
    }

    @Test
    fun `becomeGuide кидает исключение без города`() {
        val user = user(city = null)

        assertFailsWith<IllegalArgumentException> { user.becomeGuide() }
    }

    @Test
    fun `becomeGuide кидает исключение без языков`() {
        val user = user(languages = emptyList())

        assertFailsWith<IllegalArgumentException> { user.becomeGuide() }
    }

    @Test
    fun `becomeGuide идемпотентен для уже существующего гида`() {
        val user = user(isGuide = true, bio = null, city = null, languages = emptyList())

        user.becomeGuide()

        assertTrue(user.isGuide)
    }
}

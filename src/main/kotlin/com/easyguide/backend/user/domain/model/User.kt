package com.easyguide.backend.user.domain.model

import java.time.Instant
import java.util.UUID

private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

class User(
    val id: UUID,
    name: String,
    val email: String,
    val passwordHash: String,
    phone: String?,
    isGuide: Boolean,
    avatarUrl: String?,
    bio: String?,
    city: String?,
    languages: List<String>,
    val createdAt: Instant,
) {

    var name: String = name
        private set

    var phone: String? = phone
        private set

    var avatarUrl: String? = avatarUrl
        private set

    var bio: String? = bio
        private set

    var city: String? = city
        private set

    var languages: List<String> = languages.toList()
        private set

    var isGuide: Boolean = isGuide
        private set

    init {
        require(name.isNotBlank()) { "Имя не должно быть пустым" }
        require(passwordHash.isNotBlank()) { "Пароль не должен быть пустым" }
        require(EMAIL_REGEX.matches(email)) { "Почта должна быть заполнена и валидна" }
    }

    fun becomeGuide() {
        if (isGuide) return
        requireNotNull(bio) { "Описание обязательно для гидов" }
        requireNotNull(city) { "Чтобы стать гидом необходимо указать город" }
        require(languages.isNotEmpty()) { "У гида должен быть указан как минимум 1 язык" }
        isGuide = true
    }

    fun updateProfile(
        name: String,
        phone: String?,
        avatarUrl: String?,
        bio: String?,
        city: String?,
        languages: List<String>,
    ) {
        require(name.isNotBlank()) { "Имя не должно быть пустым" }

        this.name = name
        this.phone = phone
        this.avatarUrl = avatarUrl
        this.bio = bio
        this.city = city
        this.languages = languages.toList()
    }

}

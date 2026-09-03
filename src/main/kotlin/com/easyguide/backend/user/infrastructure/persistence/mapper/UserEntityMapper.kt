package com.easyguide.backend.user.infrastructure.persistence.mapper

import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.infrastructure.persistence.entity.UserEntity

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    passwordHash = passwordHash,
    name = name,
    phone = phone,
    avatarUrl = avatarUrl,
    isGuide = isGuide,
    bio = bio,
    city = city,
    languages = languages.toMutableList(),
    createdAt = createdAt,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    phone = phone,
    isGuide = isGuide,
    avatarUrl = avatarUrl,
    bio = bio,
    city = city,
    languages = languages,
    createdAt = createdAt,
)

fun UserEntity.updateFrom(user: User) {
    email = user.email
    passwordHash = user.passwordHash
    name = user.name
    phone = user.phone
    avatarUrl = user.avatarUrl
    isGuide = user.isGuide
    bio = user.bio
    city = user.city
    languages = user.languages.toMutableList()
}

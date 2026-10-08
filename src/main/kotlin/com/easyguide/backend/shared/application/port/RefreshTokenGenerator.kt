package com.easyguide.backend.shared.application.port

interface RefreshTokenGenerator {
    /** Случайный непрозрачный токен, который отдаётся клиенту. */
    fun generate(): String

    /** Детерминированный хеш токена — в БД хранится только он. */
    fun hash(token: String): String
}

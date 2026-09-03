package com.easyguide.backend.shared.application.port

import java.util.UUID

class FakeTokenIssuer : TokenIssuer {

    override fun issue(userId: UUID): String = "token:$userId"

    override fun verify(token: String): UUID? {
        val rawId = token.removePrefix("token:")
        if (rawId == token) return null
        return runCatching { UUID.fromString(rawId) }.getOrNull()
    }
}

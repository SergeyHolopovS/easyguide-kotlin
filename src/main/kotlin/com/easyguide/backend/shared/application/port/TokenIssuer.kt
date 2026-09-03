package com.easyguide.backend.shared.application.port

import java.util.UUID

interface TokenIssuer {
    fun issue(userId: UUID): String
    fun verify(token: String): UUID?
}

package com.easyguide.backend.shared.application.port

class FakeRefreshTokenGenerator : RefreshTokenGenerator {

    private var counter = 0

    override fun generate(): String = "refresh-${++counter}"

    override fun hash(token: String): String = "hashed:$token"
}

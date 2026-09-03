package com.easyguide.backend.shared.application.port

class FakePasswordHasher : PasswordHasher {

    override fun hash(rawPassword: String): String = "hashed:$rawPassword"

    override fun matches(rawPassword: String, hashedPassword: String): Boolean =
        hashedPassword == hash(rawPassword)
}

package com.easyguide.backend.shared.infrastructure.security

import com.easyguide.backend.shared.application.port.TokenIssuer
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.UUID

@Component
class JwtTokenIssuer(
    @Value($$"${app.jwt.secret}") secret: String,
    @Value($$"${app.jwt.expiration-minutes:60}") private val expirationMinutes: Long,
) : TokenIssuer {

    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    override fun issue(userId: UUID): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(Duration.ofMinutes(expirationMinutes))))
            .signWith(key)
            .compact()
    }

    override fun verify(token: String): UUID? {
        val subject = try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (_: JwtException) {
            null
        } ?: return null

        return try {
            UUID.fromString(subject)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}

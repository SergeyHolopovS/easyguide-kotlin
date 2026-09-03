package com.easyguide.backend.shared.application.port

import java.time.Instant

class FakeClock(
    private val fixed: Instant = Instant.parse("2026-01-01T00:00:00Z"),
) : Clock {

    override fun now(): Instant = fixed
}

package com.easyguide.backend.shared.infrastructure.time

import com.easyguide.backend.shared.application.port.Clock
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SystemClock : Clock {
    override fun now(): Instant = Instant.now()
}

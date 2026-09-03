package com.easyguide.backend.shared.application.port

import java.time.Instant

interface Clock {
    fun now(): Instant
}

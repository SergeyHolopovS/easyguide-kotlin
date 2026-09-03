package com.easyguide.backend.review.domain.model

import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ReviewTest {

    private fun review(rating: Int): Review = Review(
        id = UUID.randomUUID(),
        tourId = UUID.randomUUID(),
        authorId = UUID.randomUUID(),
        bookingId = UUID.randomUUID(),
        rating = rating,
        comment = "Отлично",
        createdAt = Instant.now(),
    )

    @Test
    fun `rating 0 кидает исключение`() {
        assertFailsWith<IllegalArgumentException> { review(0) }
    }

    @Test
    fun `rating 6 кидает исключение`() {
        assertFailsWith<IllegalArgumentException> { review(6) }
    }
}

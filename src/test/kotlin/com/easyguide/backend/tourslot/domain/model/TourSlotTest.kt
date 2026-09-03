package com.easyguide.backend.tourslot.domain.model

import com.easyguide.backend.tourslot.domain.exception.SlotFullException
import com.easyguide.backend.tourslot.domain.exception.SlotNotBookableException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TourSlotTest {

    private val now: Instant = Instant.parse("2026-09-01T10:00:00Z")

    private fun slot(
        startsAt: Instant = now.plus(3, ChronoUnit.HOURS),
        capacity: Int = 5,
        bookedSeats: Int = 0,
        isCancelled: Boolean = false,
    ): TourSlot = TourSlot(
        id = UUID.randomUUID(),
        tourId = UUID.randomUUID(),
        startsAt = startsAt,
        capacity = capacity,
        bookedSeats = bookedSeats,
        isCancelled = isCancelled,
    )

    @Test
    fun `isBookable возвращает true когда всё в порядке`() {
        val slot = slot()

        assertTrue(slot.isBookable(now))
    }

    @Test
    fun `isBookable возвращает false если мест не осталось`() {
        val slot = slot(capacity = 3, bookedSeats = 3)

        assertFalse(slot.isBookable(now))
    }

    @Test
    fun `isBookable возвращает false если слот отменён`() {
        val slot = slot(isCancelled = true)

        assertFalse(slot.isBookable(now))
    }

    @Test
    fun `isBookable возвращает false если до старта меньше 2 часов`() {
        val slot = slot(startsAt = now.plus(1, ChronoUnit.HOURS))

        assertFalse(slot.isBookable(now))
    }

    @Test
    fun `ensureBookable не кидает исключение когда всё в порядке`() {
        val slot = slot()

        slot.ensureBookable(now)
    }

    @Test
    fun `ensureBookable кидает SlotFullException если мест не осталось`() {
        val slot = slot(capacity = 3, bookedSeats = 3)

        assertFailsWith<SlotFullException> { slot.ensureBookable(now) }
    }

    @Test
    fun `ensureBookable кидает SlotNotBookableException если слот отменён`() {
        val slot = slot(isCancelled = true)

        assertFailsWith<SlotNotBookableException> { slot.ensureBookable(now) }
    }

    @Test
    fun `ensureBookable кидает SlotNotBookableException если до старта меньше 2 часов`() {
        val slot = slot(startsAt = now.plus(1, ChronoUnit.HOURS))

        assertFailsWith<SlotNotBookableException> { slot.ensureBookable(now) }
    }
}

package com.easyguide.backend.tour.domain.model

import com.easyguide.backend.tour.domain.exception.TourNotReadyException
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TourTest {

    private fun tour(
        description: String = "Отличная экскурсия по историческому центру города с профессиональным гидом",
        price: BigDecimal? = BigDecimal("100.00"),
        photos: List<TourPhoto> = listOf(TourPhoto(id = UUID.randomUUID(), url = "https://example.com/photo.jpg")),
    ): Tour = Tour(
        id = UUID.randomUUID(),
        guideId = UUID.randomUUID(),
        title = "Прогулка по городу",
        description = description,
        city = "Москва",
        category = TourCategory.WALKING,
        meetingPoint = "Красная площадь",
        timezone = ZoneId.of("Europe/Moscow"),
        durationMinutes = 120,
        price = price,
        maxPeople = 10,
        photos = photos,
    )

    @Test
    fun `publish переводит полностью заполненный тур в PUBLISHED`() {
        val tour = tour()

        tour.publish()

        assertEquals(TourStatus.PUBLISHED, tour.status)
    }

    @Test
    fun `publish кидает исключение со списком незаполненных полей`() {
        val tour = tour(description = "коротко", price = null, photos = emptyList())

        val exception = assertFailsWith<TourNotReadyException> { tour.publish() }

        assertEquals(listOf("description", "price", "photos"), exception.missingFields)
    }
}

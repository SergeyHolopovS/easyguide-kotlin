package com.easyguide.backend.review.infrastructure.persistence.adapter

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.infrastructure.persistence.adapter.BookingRepositoryAdapter
import com.easyguide.backend.booking.infrastructure.persistence.jpa.BookingJpaRepository
import com.easyguide.backend.review.domain.model.Review
import com.easyguide.backend.review.infrastructure.persistence.jpa.ReviewJpaRepository
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.infrastructure.persistence.adapter.TourRepositoryAdapter
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourJpaRepository
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourPhotoJpaRepository
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.infrastructure.persistence.adapter.TourSlotRepositoryAdapter
import com.easyguide.backend.tourslot.infrastructure.persistence.jpa.TourSlotJpaRepository
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.infrastructure.persistence.adapter.UserRepositoryAdapter
import com.easyguide.backend.user.infrastructure.persistence.jpa.UserJpaRepository
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration::class)
class ReviewRepositoryAdapterTest {

    @Autowired
    private lateinit var reviewJpaRepository: ReviewJpaRepository

    @Autowired
    private lateinit var bookingJpaRepository: BookingJpaRepository

    @Autowired
    private lateinit var slotJpaRepository: TourSlotJpaRepository

    @Autowired
    private lateinit var tourJpaRepository: TourJpaRepository

    @Autowired
    private lateinit var tourPhotoJpaRepository: TourPhotoJpaRepository

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private val adapter by lazy { ReviewRepositoryAdapter(reviewJpaRepository) }
    private val bookingAdapter by lazy { BookingRepositoryAdapter(bookingJpaRepository) }
    private val slotAdapter by lazy { TourSlotRepositoryAdapter(slotJpaRepository) }
    private val tourAdapter by lazy { TourRepositoryAdapter(tourJpaRepository, tourPhotoJpaRepository) }
    private val userAdapter by lazy { UserRepositoryAdapter(userJpaRepository) }

    private fun persistedUserId(): UUID {
        val user = User(
            id = UUID.randomUUID(),
            name = "Турист",
            email = "tourist-${UUID.randomUUID()}@example.com",
            passwordHash = "hash",
            phone = null,
            isGuide = false,
            avatarUrl = null,
            bio = null,
            city = null,
            languages = emptyList(),
            createdAt = Instant.now(),
        )
        userAdapter.save(user)
        return user.id
    }

    private fun persistedTourId(): UUID {
        val guide = User(
            id = UUID.randomUUID(),
            name = "Гид",
            email = "guide-${UUID.randomUUID()}@example.com",
            passwordHash = "hash",
            phone = null,
            isGuide = true,
            avatarUrl = null,
            bio = "Бывалый",
            city = "Москва",
            languages = listOf("ru"),
            createdAt = Instant.now(),
        )
        userAdapter.save(guide)

        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guide.id,
            title = "Тур",
            description = "Описание тура подлиннее пятидесяти символов для валидации полей",
            city = "Москва",
            category = TourCategory.WALKING,
            meetingPoint = "Точка встречи",
            timezone = ZoneId.of("Europe/Moscow"),
            durationMinutes = 60,
            price = BigDecimal("500.00"),
            maxPeople = 5,
            status = TourStatus.DRAFT,
        )
        tourAdapter.save(tour)
        return tour.id
    }

    private fun persistedBookingId(tourId: UUID): UUID {
        val slot = TourSlot(id = UUID.randomUUID(), tourId = tourId, startsAt = Instant.now().plusSeconds(3600), capacity = 5)
        slotAdapter.save(slot)

        val booking = Booking(
            id = UUID.randomUUID(),
            slotId = slot.id,
            userId = persistedUserId(),
            seats = 1,
            totalPrice = BigDecimal("500.00"),
            contactPhone = null,
            comment = null,
            createdAt = Instant.now(),
        )
        bookingAdapter.save(booking)
        return booking.id
    }

    private fun review(tourId: UUID, bookingId: UUID, authorId: UUID, rating: Int): Review = Review(
        id = UUID.randomUUID(),
        tourId = tourId,
        authorId = authorId,
        bookingId = bookingId,
        rating = rating,
        comment = "Отличная экскурсия",
        createdAt = Instant.now(),
    )

    @Test
    fun `save и findByBookingId возвращают те же поля`() {
        val tourId = persistedTourId()
        val bookingId = persistedBookingId(tourId)
        val authorId = persistedUserId()
        val review = review(tourId, bookingId, authorId, rating = 5)

        adapter.save(review)
        entityManager.flush()
        entityManager.clear()

        val loaded = adapter.findByBookingId(bookingId)

        assertEquals(review.id, loaded?.id)
        assertEquals(review.tourId, loaded?.tourId)
        assertEquals(review.authorId, loaded?.authorId)
        assertEquals(review.bookingId, loaded?.bookingId)
        assertEquals(review.rating, loaded?.rating)
        assertEquals(review.comment, loaded?.comment)
    }

    @Test
    fun `calcRating считает среднее и число отзывов по туру`() {
        val tourId = persistedTourId()

        val b1 = persistedBookingId(tourId)
        val b2 = persistedBookingId(tourId)

        adapter.save(review(tourId, b1, persistedUserId(), rating = 4))
        adapter.save(review(tourId, b2, persistedUserId(), rating = 2))
        entityManager.flush()
        entityManager.clear()

        val summary = adapter.calcRating(tourId)

        assertEquals(3.0, summary.average)
        assertEquals(2, summary.count)
    }

    @Test
    fun `второй отзыв к той же брони нарушает unique-констрейнт`() {
        val tourId = persistedTourId()
        val bookingId = persistedBookingId(tourId)

        adapter.save(review(tourId, bookingId, persistedUserId(), rating = 5))
        entityManager.flush()

        assertFailsWith<ConstraintViolationException> {
            adapter.save(review(tourId, bookingId, persistedUserId(), rating = 1))
            entityManager.flush()
        }
    }
}

package com.easyguide.backend.booking.infrastructure.persistence.adapter

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.infrastructure.persistence.jpa.BookingJpaRepository
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
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration::class)
class BookingRepositoryAdapterTest {

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

    private val adapter by lazy { BookingRepositoryAdapter(bookingJpaRepository) }
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

    private fun persistedSlot(startsAt: Instant = Instant.now().plusSeconds(3600)): UUID {
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

        val slot = TourSlot(id = UUID.randomUUID(), tourId = tour.id, startsAt = startsAt, capacity = 5)
        slotAdapter.save(slot)
        return slot.id
    }

    private fun booking(userId: UUID, slotId: UUID, status: BookingStatus = BookingStatus.PENDING): Booking = Booking(
        id = UUID.randomUUID(),
        slotId = slotId,
        userId = userId,
        seats = 2,
        totalPrice = BigDecimal("1000.00"),
        contactPhone = "+79990001122",
        comment = "Позвоните за час",
        createdAt = Instant.now(),
        status = status,
    )

    @Test
    fun `save и findById возвращают те же поля`() {
        val userId = persistedUserId()
        val slotId = persistedSlot()
        val booking = booking(userId, slotId)
        booking.confirm()
        booking.complete()

        adapter.save(booking)
        entityManager.flush()
        entityManager.clear()

        val loaded = adapter.findById(booking.id)

        assertEquals(booking.id, loaded?.id)
        assertEquals(booking.slotId, loaded?.slotId)
        assertEquals(booking.userId, loaded?.userId)
        assertEquals(booking.seats, loaded?.seats)
        assertEquals(0, booking.totalPrice.compareTo(loaded!!.totalPrice))
        assertEquals(booking.contactPhone, loaded.contactPhone)
        assertEquals(booking.comment, loaded.comment)
        assertEquals(booking.status, loaded.status)
    }

    @Test
    fun `existsActiveByUserAndSlot и findActiveBySlot видят только активные брони`() {
        val userId = persistedUserId()
        val slotId = persistedSlot()

        val active = booking(userId, slotId)
        adapter.save(active)

        val rejected = booking(persistedUserId(), slotId)
        rejected.reject()
        adapter.save(rejected)

        entityManager.flush()
        entityManager.clear()

        assertTrue(adapter.existsActiveByUserAndSlot(userId, slotId))
        val activeBookings = adapter.findActiveBySlot(slotId)
        assertEquals(1, activeBookings.size)
        assertEquals(active.id, activeBookings.first().id)
    }

    @Test
    fun `findConfirmedFinishedBefore находит подтверждённые брони на прошедшие слоты`() {
        val pastSlotId = persistedSlot(startsAt = Instant.now().minusSeconds(3600))
        val futureSlotId = persistedSlot(startsAt = Instant.now().plusSeconds(3600))

        val pastBooking = booking(persistedUserId(), pastSlotId)
        pastBooking.confirm()
        adapter.save(pastBooking)

        val futureBooking = booking(persistedUserId(), futureSlotId)
        futureBooking.confirm()
        adapter.save(futureBooking)

        entityManager.flush()
        entityManager.clear()

        val finished = adapter.findConfirmedFinishedBefore(Instant.now(), limit = 500)

        assertEquals(listOf(pastBooking.id), finished.map { it.id })
    }
}

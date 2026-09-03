package com.easyguide.backend.tourslot.infrastructure.persistence.adapter

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.infrastructure.persistence.adapter.TourRepositoryAdapter
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourJpaRepository
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourPhotoJpaRepository
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.infrastructure.persistence.jpa.TourSlotJpaRepository
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.infrastructure.persistence.adapter.UserRepositoryAdapter
import com.easyguide.backend.user.infrastructure.persistence.jpa.UserJpaRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration::class)
class TourSlotRepositoryAdapterTest {

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

    private val adapter by lazy { TourSlotRepositoryAdapter(slotJpaRepository) }
    private val tourAdapter by lazy { TourRepositoryAdapter(tourJpaRepository, tourPhotoJpaRepository) }
    private val userAdapter by lazy { UserRepositoryAdapter(userJpaRepository) }

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

    private fun slot(
        tourId: UUID,
        capacity: Int = 5,
        bookedSeats: Int = 0,
        startsAt: Instant = Instant.now().plusSeconds(3600),
    ): TourSlot = TourSlot(
        id = UUID.randomUUID(),
        tourId = tourId,
        startsAt = startsAt,
        capacity = capacity,
        bookedSeats = bookedSeats,
    )

    @Test
    fun `save и findById возвращают те же поля`() {
        val tourId = persistedTourId()
        val slot = slot(tourId)

        adapter.save(slot)
        entityManager.flush()
        entityManager.clear()

        val loaded = adapter.findById(slot.id)

        assertEquals(slot.id, loaded?.id)
        assertEquals(slot.tourId, loaded?.tourId)
        assertEquals(slot.startsAt, loaded?.startsAt)
        assertEquals(slot.capacity, loaded?.capacity)
        assertEquals(slot.bookedSeats, loaded?.bookedSeats)
        assertEquals(slot.isCancelled, loaded?.isCancelled)
    }

    @Test
    fun `tryReserveSeats занимает места когда их достаточно`() {
        val tourId = persistedTourId()
        val slot = slot(tourId, capacity = 5, bookedSeats = 0)
        adapter.save(slot)
        entityManager.flush()
        entityManager.clear()

        val reserved = adapter.tryReserveSeats(slot.id, 3)

        assertTrue(reserved)
        entityManager.clear()
        assertEquals(3, adapter.findById(slot.id)?.bookedSeats)
    }

    @Test
    fun `tryReserveSeats не занимает места когда их не хватает и данные не меняются`() {
        val tourId = persistedTourId()
        val slot = slot(tourId, capacity = 5, bookedSeats = 4)
        adapter.save(slot)
        entityManager.flush()
        entityManager.clear()

        val reserved = adapter.tryReserveSeats(slot.id, 2)

        assertFalse(reserved)
        entityManager.clear()
        assertEquals(4, adapter.findById(slot.id)?.bookedSeats)
    }

    @Test
    fun `releaseSeats не уводит booked_seats ниже нуля`() {
        val tourId = persistedTourId()
        val slot = slot(tourId, capacity = 5, bookedSeats = 1)
        adapter.save(slot)
        entityManager.flush()
        entityManager.clear()

        adapter.releaseSeats(slot.id, 5)

        entityManager.clear()
        assertEquals(0, adapter.findById(slot.id)?.bookedSeats)
    }

    @Test
    fun `дубль tour_id + starts_at нарушает unique-констрейнт`() {
        val tourId = persistedTourId()
        val startsAt = Instant.now().plusSeconds(7200)

        adapter.save(slot(tourId, startsAt = startsAt))
        entityManager.flush()

        assertFailsWith<DataIntegrityViolationException> {
            adapter.save(slot(tourId, startsAt = startsAt))
            entityManager.flush()
        }
    }
}

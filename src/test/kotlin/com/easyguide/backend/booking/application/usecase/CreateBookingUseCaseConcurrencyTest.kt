package com.easyguide.backend.booking.application.usecase

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.booking.application.dto.CreateBookingCommand
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.exception.SlotFullException
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class CreateBookingUseCaseConcurrencyTest {

    @Autowired
    private lateinit var createBookingUseCase: CreateBookingUseCase

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tourRepository: TourRepository

    @Autowired
    private lateinit var slotRepository: SlotRepository

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    private fun user(isGuide: Boolean): User {
        val user = User(
            id = UUID.randomUUID(),
            name = if (isGuide) "Гид" else "Турист",
            email = "${if (isGuide) "guide" else "traveler"}-${UUID.randomUUID()}@example.com",
            passwordHash = "hash",
            phone = null,
            isGuide = isGuide,
            avatarUrl = null,
            bio = if (isGuide) "Опытный гид" else null,
            city = if (isGuide) "Москва" else null,
            languages = if (isGuide) listOf("ru") else emptyList(),
            createdAt = Instant.now(),
        )
        return userRepository.save(user)
    }

    @Test
    fun `10 потоков одновременно бронируют слот на 5 мест - ровно 5 успехов и 5 SlotFullException`() {
        val guide = user(isGuide = true)

        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guide.id,
            title = "Тур на пятерых",
            description = "Описание тура подлиннее пятидесяти символов для валидации полей",
            city = "Москва",
            category = TourCategory.WALKING,
            meetingPoint = "Точка встречи",
            timezone = ZoneId.of("Europe/Moscow"),
            durationMinutes = 60,
            price = BigDecimal("1000.00"),
            maxPeople = 5,
            status = TourStatus.PUBLISHED,
        )
        tourRepository.save(tour)

        val slot = TourSlot(
            id = UUID.randomUUID(),
            tourId = tour.id,
            startsAt = Instant.now().plus(3, ChronoUnit.HOURS),
            capacity = 5,
        )
        slotRepository.save(slot)

        val travelerCount = 10
        val travelers = (1..travelerCount).map { user(isGuide = false) }

        val readyLatch = CountDownLatch(travelerCount)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(travelerCount)

        val successCount = AtomicInteger(0)
        val slotFullCount = AtomicInteger(0)
        val unexpectedErrors = java.util.Collections.synchronizedList(mutableListOf<Throwable>())

        val executor = Executors.newFixedThreadPool(travelerCount)

        travelers.forEach { traveler ->
            executor.submit {
                readyLatch.countDown()
                startLatch.await()
                try {
                    createBookingUseCase.execute(
                        traveler.id,
                        CreateBookingCommand(
                            slotId = slot.id,
                            seats = 1,
                            contactPhone = "+79990000000",
                            comment = null,
                        ),
                    )
                    successCount.incrementAndGet()
                } catch (e: SlotFullException) {
                    slotFullCount.incrementAndGet()
                } catch (e: Throwable) {
                    unexpectedErrors.add(e)
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        assertTrue(readyLatch.await(10, TimeUnit.SECONDS), "потоки не успели стартовать вовремя")
        startLatch.countDown()
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "потоки не завершились вовремя")
        executor.shutdown()

        assertTrue(unexpectedErrors.isEmpty(), "Неожиданные исключения: $unexpectedErrors")

        assertEquals(5, successCount.get())
        assertEquals(5, slotFullCount.get())

        val reloadedSlot = slotRepository.findById(slot.id)
        assertEquals(5, reloadedSlot?.bookedSeats)

        val activeBookings = bookingRepository.findActiveBySlot(slot.id)
        assertEquals(5, activeBookings.size)
    }
}

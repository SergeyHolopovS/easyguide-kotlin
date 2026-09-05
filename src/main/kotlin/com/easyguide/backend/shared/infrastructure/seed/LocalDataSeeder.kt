package com.easyguide.backend.shared.infrastructure.seed

import com.easyguide.backend.booking.domain.model.Booking
import com.easyguide.backend.booking.domain.model.BookingStatus
import com.easyguide.backend.booking.domain.model.CancelledBy
import com.easyguide.backend.booking.domain.repository.BookingRepository
import com.easyguide.backend.review.domain.model.Review
import com.easyguide.backend.review.domain.repository.ReviewRepository
import com.easyguide.backend.shared.application.port.Clock
import com.easyguide.backend.shared.application.port.PasswordHasher
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.tourslot.domain.model.TourSlot
import com.easyguide.backend.tourslot.domain.repository.SlotRepository
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

/** Пароль одинаковый для всех сидовых аккаунтов (гидов и путешественников) — см. README. */
private const val SEED_PASSWORD = "Passw0rd!2026"

private val ZONE: ZoneId = ZoneId.of("Europe/Moscow")

/** Дни вперёд от сегодня, на которые заводятся будущие слоты у каждого тура — покрывает месяц вперёд. */
private val FUTURE_OFFSETS_DAYS = listOf(2L, 7L, 12L, 17L, 22L, 27L)

private data class SeedGuide(
    val name: String,
    val email: String,
    val city: String,
    val bio: String,
    val avatarUrl: String,
)

private data class SeedTour(
    val guideIndex: Int,
    val title: String,
    val description: String,
    val category: TourCategory,
    val meetingPoint: String,
    val durationMinutes: Int,
    val price: BigDecimal,
    val maxPeople: Int,
    val photoUrls: List<String>,
)

private val GUIDES_DATA = listOf(
    SeedGuide(
        name = "Анна Иванова",
        email = "guide1@easyguide.local",
        city = "Москва",
        bio = "Провожу авторские пешие экскурсии по историческому центру уже 8 лет.",
        avatarUrl = "https://i.pravatar.cc/300?img=47",
    ),
    SeedGuide(
        name = "Дмитрий Соколов",
        email = "guide2@easyguide.local",
        city = "Санкт-Петербург",
        bio = "Гид-краевед, специализируюсь на дворцах и набережных Северной столицы.",
        avatarUrl = "https://i.pravatar.cc/300?img=12",
    ),
    SeedGuide(
        name = "Мария Петрова",
        email = "guide3@easyguide.local",
        city = "Казань",
        bio = "Показываю гостям многоликую Казань — от Кремля до татарской кухни.",
        avatarUrl = "https://i.pravatar.cc/300?img=32",
    ),
)

private val TRAVELERS_DATA = listOf(
    "Иван Смирнов" to "traveler1@easyguide.local",
    "Ольга Кузнецова" to "traveler2@easyguide.local",
)

private val TOURS_DATA = listOf(
    SeedTour(
        guideIndex = 0,
        title = "Пешая прогулка по Красной площади и Кремлю",
        description = "Пройдём по главной площади страны, узнаем историю Кремля, соборов и Мавзолея. Много деталей и легенд.",
        category = TourCategory.WALKING,
        meetingPoint = "У памятника Минину и Пожарскому",
        durationMinutes = 120,
        price = BigDecimal("1500.00"),
        maxPeople = 12,
        photoUrls = listOf("https://picsum.photos/seed/tour0-1/800/600", "https://picsum.photos/seed/tour0-2/800/600"),
    ),
    SeedTour(
        guideIndex = 0,
        title = "Гастрономический тур по Арбату",
        description = "Дегустация классических блюд московской кухни в старейших заведениях Арбата и окрестностей.",
        category = TourCategory.FOOD,
        meetingPoint = "У памятника Булату Окуджаве",
        durationMinutes = 150,
        price = BigDecimal("2500.00"),
        maxPeople = 8,
        photoUrls = listOf("https://picsum.photos/seed/tour1-1/800/600", "https://picsum.photos/seed/tour1-2/800/600"),
    ),
    SeedTour(
        guideIndex = 0,
        title = "Секреты московского метро",
        description = "Осмотрим самые красивые станции метро и узнаем, какие тайны скрывают их мозаики и скульптуры.",
        category = TourCategory.HISTORY,
        meetingPoint = "Станция метро Комсомольская, у выхода в город",
        durationMinutes = 90,
        price = BigDecimal("1200.00"),
        maxPeople = 15,
        photoUrls = listOf("https://picsum.photos/seed/tour2-1/800/600"),
    ),
    SeedTour(
        guideIndex = 0,
        title = "Ночная Москва: огни мегаполиса",
        description = "Вечерний маршрут по набережным и смотровым площадкам с видом на подсвеченные высотки и мосты.",
        category = TourCategory.NIGHTLIFE,
        meetingPoint = "У Патриаршего моста",
        durationMinutes = 120,
        price = BigDecimal("1800.00"),
        maxPeople = 10,
        photoUrls = listOf("https://picsum.photos/seed/tour3-1/800/600", "https://picsum.photos/seed/tour3-2/800/600"),
    ),
    SeedTour(
        guideIndex = 1,
        title = "Дворцы и каналы Петербурга",
        description = "Прогулка вдоль набережных с рассказом об архитектуре дворцов и истории водных каналов города.",
        category = TourCategory.CULTURE,
        meetingPoint = "У Дворцовой площади",
        durationMinutes = 150,
        price = BigDecimal("2000.00"),
        maxPeople = 10,
        photoUrls = listOf("https://picsum.photos/seed/tour4-1/800/600", "https://picsum.photos/seed/tour4-2/800/600"),
    ),
    SeedTour(
        guideIndex = 1,
        title = "Эрмитаж и окрестности",
        description = "Обзорная экскурсия по главным залам Эрмитажа с погружением в историю императорской коллекции.",
        category = TourCategory.HISTORY,
        meetingPoint = "У главного входа в Эрмитаж",
        durationMinutes = 180,
        price = BigDecimal("2800.00"),
        maxPeople = 12,
        photoUrls = listOf("https://picsum.photos/seed/tour5-1/800/600"),
    ),
    SeedTour(
        guideIndex = 1,
        title = "Белые ночи: вечерний Петербург",
        description = "Вечерний маршрут по разводным мостам и набережным во время белых ночей с видом на закат.",
        category = TourCategory.NIGHTLIFE,
        meetingPoint = "У Дворцового моста",
        durationMinutes = 120,
        price = BigDecimal("1900.00"),
        maxPeople = 10,
        photoUrls = listOf("https://picsum.photos/seed/tour6-1/800/600", "https://picsum.photos/seed/tour6-2/800/600"),
    ),
    SeedTour(
        guideIndex = 2,
        title = "Казанский Кремль и Старо-Татарская слобода",
        description = "Осмотрим Кремль, мечеть Кул-Шариф и прогуляемся по колоритной Старо-Татарской слободе.",
        category = TourCategory.HISTORY,
        meetingPoint = "У главных ворот Казанского Кремля",
        durationMinutes = 120,
        price = BigDecimal("1400.00"),
        maxPeople = 14,
        photoUrls = listOf("https://picsum.photos/seed/tour7-1/800/600", "https://picsum.photos/seed/tour7-2/800/600"),
    ),
    SeedTour(
        guideIndex = 2,
        title = "Вкус Татарстана: гастротур",
        description = "Попробуем эчпочмак, чак-чак и другие блюда татарской кухни в аутентичных заведениях города.",
        category = TourCategory.FOOD,
        meetingPoint = "У Казанского цирка",
        durationMinutes = 150,
        price = BigDecimal("2200.00"),
        maxPeople = 8,
        photoUrls = listOf("https://picsum.photos/seed/tour8-1/800/600"),
    ),
    SeedTour(
        guideIndex = 2,
        title = "Волга и природа вокруг Казани",
        description = "Загородная поездка на берег Волги с прогулкой по природному парку и рассказом о местной флоре.",
        category = TourCategory.NATURE,
        meetingPoint = "У речного порта",
        durationMinutes = 240,
        price = BigDecimal("3000.00"),
        maxPeople = 10,
        photoUrls = listOf("https://picsum.photos/seed/tour9-1/800/600", "https://picsum.photos/seed/tour9-2/800/600"),
    ),
)

@Component
@Profile("local")
class LocalDataSeeder(
    private val userRepository: UserRepository,
    private val tourRepository: TourRepository,
    private val slotRepository: SlotRepository,
    private val bookingRepository: BookingRepository,
    private val reviewRepository: ReviewRepository,
    private val passwordHasher: PasswordHasher,
    private val clock: Clock,
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String) {
        if (userRepository.existsByEmail(GUIDES_DATA.first().email)) {
            logger.info { "Тестовые данные уже загружены — пропускаю сидинг" }
            return
        }

        logger.info { "Загружаю тестовые данные (профиль local)..." }

        val passwordHash = passwordHasher.hash(SEED_PASSWORD)
        val now = clock.now()

        val guides = GUIDES_DATA.map { data ->
            userRepository.save(
                User(
                    id = UUID.randomUUID(),
                    name = data.name,
                    email = data.email,
                    passwordHash = passwordHash,
                    phone = null,
                    isGuide = true,
                    avatarUrl = data.avatarUrl,
                    bio = data.bio,
                    city = data.city,
                    languages = listOf("ru", "en"),
                    createdAt = now,
                ),
            )
        }

        val travelers = TRAVELERS_DATA.map { (name, email) ->
            userRepository.save(
                User(
                    id = UUID.randomUUID(),
                    name = name,
                    email = email,
                    passwordHash = passwordHash,
                    phone = null,
                    isGuide = false,
                    avatarUrl = null,
                    bio = null,
                    city = null,
                    languages = emptyList(),
                    createdAt = now,
                ),
            )
        }

        val tours = TOURS_DATA.map { data ->
            val guide = guides[data.guideIndex]
            val tour = Tour(
                id = UUID.randomUUID(),
                guideId = guide.id,
                title = data.title,
                description = data.description,
                city = guide.city!!,
                category = data.category,
                meetingPoint = data.meetingPoint,
                timezone = ZONE,
                durationMinutes = data.durationMinutes,
                price = data.price,
                maxPeople = data.maxPeople,
            )
            data.photoUrls.forEach { tour.addPhoto(it) }
            tour.publish()
            tourRepository.save(tour)
        }

        // будущие слоты на месяц вперёд — для всех туров, чтобы каждый тур можно было забронировать
        val futureSlots = tours.map { tour ->
            FUTURE_OFFSETS_DAYS.map { offset -> saveSlot(tour, offset) }
        }

        fun pastSlot(tour: Tour, daysAgo: Long) = saveSlot(tour, -daysAgo)

        fun booking(
            traveler: User,
            tour: Tour,
            slot: TourSlot,
            seats: Int,
            status: BookingStatus,
            cancelledBy: CancelledBy? = null,
            cancelReason: String? = null,
        ): Booking {
            // реальный жизненный цикл мест: резервируем, а для отклонённых/отменённых — сразу освобождаем
            slotRepository.tryReserveSeats(slot.id, seats)
            if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
                slotRepository.releaseSeats(slot.id, seats)
            }

            return bookingRepository.save(
                Booking(
                    id = UUID.randomUUID(),
                    slotId = slot.id,
                    userId = traveler.id,
                    seats = seats,
                    totalPrice = tour.price!!.multiply(BigDecimal(seats)),
                    contactPhone = "+7 900 000-00-00",
                    comment = null,
                    createdAt = now,
                    status = status,
                    cancelledBy = cancelledBy,
                    cancelReason = cancelReason,
                ),
            )
        }

        fun review(traveler: User, tour: Tour, booking: Booking, rating: Int, text: String) {
            reviewRepository.save(
                Review(
                    id = UUID.randomUUID(),
                    tourId = tour.id,
                    authorId = traveler.id,
                    bookingId = booking.id,
                    rating = rating,
                    comment = text,
                    createdAt = now,
                ),
            )
            val summary = reviewRepository.calcRating(tour.id)
            tour.updateRating(summary.average, summary.count)
            tourRepository.save(tour)
        }

        // 4 брони "в процессе" — по одной на каждый статус, кроме COMPLETED
        booking(travelers[0], tours[0], futureSlots[0][0], seats = 2, status = BookingStatus.PENDING)
        booking(travelers[0], tours[1], futureSlots[1][0], seats = 1, status = BookingStatus.CONFIRMED)
        booking(travelers[1], tours[4], futureSlots[4][0], seats = 3, status = BookingStatus.REJECTED)
        booking(
            travelers[1], tours[0], futureSlots[0][1], seats = 1, status = BookingStatus.CANCELLED,
            cancelledBy = CancelledBy.TOURIST, cancelReason = "Изменились планы",
        )

        // 6 завершённых броней: 5 с отзывом, 1 — намеренно без отзыва
        val completed1 = booking(travelers[0], tours[0], pastSlot(tours[0], 5), seats = 2, status = BookingStatus.COMPLETED)
        review(travelers[0], tours[0], completed1, rating = 5, text = "Отличная прогулка, гид очень много знает!")

        val completedWithoutReview = booking(travelers[1], tours[4], pastSlot(tours[4], 7), seats = 1, status = BookingStatus.COMPLETED)
        logger.info { "Бронь ${completedWithoutReview.id} завершена намеренно без отзыва" }

        val completed2 = booking(travelers[0], tours[4], pastSlot(tours[4], 14), seats = 1, status = BookingStatus.COMPLETED)
        review(travelers[0], tours[4], completed2, rating = 4, text = "Красиво, но группа была большая.")

        val completed3 = booking(travelers[1], tours[7], pastSlot(tours[7], 6), seats = 2, status = BookingStatus.COMPLETED)
        review(travelers[1], tours[7], completed3, rating = 5, text = "Незабываемая экскурсия по Кремлю, всем советую.")

        val completed4 = booking(travelers[0], tours[7], pastSlot(tours[7], 20), seats = 1, status = BookingStatus.COMPLETED)
        review(travelers[0], tours[7], completed4, rating = 3, text = "Интересно, но было холодно и долго стояли на улице.")

        val completed5 = booking(travelers[1], tours[1], pastSlot(tours[1], 10), seats = 2, status = BookingStatus.COMPLETED)
        review(travelers[1], tours[1], completed5, rating = 4, text = "Вкусно и сытно, гид знает лучшие места.")

        logger.info {
            "Тестовые данные загружены: ${guides.size} гидов, ${travelers.size} путешественников, " +
                "${tours.size} туров, 10 броней, 5 отзывов. Пароль для всех аккаунтов — см. README."
        }
    }

    private fun saveSlot(tour: Tour, offsetDays: Long): TourSlot {
        val startsAt = ZonedDateTime.now(ZONE)
            .plusDays(offsetDays)
            .withHour(10).withMinute(0).withSecond(0).withNano(0)
            .toInstant()

        return slotRepository.save(
            TourSlot(id = UUID.randomUUID(), tourId = tour.id, startsAt = startsAt, capacity = tour.maxPeople),
        )
    }
}

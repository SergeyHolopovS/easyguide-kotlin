package com.easyguide.backend.tour.infrastructure.persistence.adapter

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourPhoto
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourJpaRepository
import com.easyguide.backend.tour.infrastructure.persistence.jpa.TourPhotoJpaRepository
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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration::class)
class TourRepositoryAdapterTest {

    @Autowired
    private lateinit var tourJpaRepository: TourJpaRepository

    @Autowired
    private lateinit var tourPhotoJpaRepository: TourPhotoJpaRepository

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private val adapter by lazy { TourRepositoryAdapter(tourJpaRepository, tourPhotoJpaRepository) }
    private val userAdapter by lazy { UserRepositoryAdapter(userJpaRepository) }

    private fun persistedGuideId(): UUID {
        val guide = User(
            id = UUID.randomUUID(),
            name = "Гид Гидов",
            email = "guide-${UUID.randomUUID()}@example.com",
            passwordHash = "hash",
            phone = null,
            isGuide = true,
            avatarUrl = null,
            bio = "Опытный гид",
            city = "Москва",
            languages = listOf("ru"),
            createdAt = Instant.now(),
        )
        userAdapter.save(guide)
        return guide.id
    }

    private fun tour(
        guideId: UUID,
        photos: List<TourPhoto> = listOf(TourPhoto(id = UUID.randomUUID(), url = "https://example.com/1.jpg", sortOrder = 0)),
    ): Tour = Tour(
        id = UUID.randomUUID(),
        guideId = guideId,
        title = "Прогулка по центру",
        description = "Подробная экскурсия по историческому центру города с профессиональным гидом",
        city = "Москва",
        category = TourCategory.WALKING,
        meetingPoint = "Красная площадь",
        timezone = ZoneId.of("Europe/Moscow"),
        durationMinutes = 120,
        price = BigDecimal("1500.00"),
        maxPeople = 10,
        status = TourStatus.DRAFT,
        photos = photos,
    )

    @Test
    fun `save и findById возвращают те же поля включая фото`() {
        val guideId = persistedGuideId()
        val tour = tour(guideId)

        adapter.save(tour)
        entityManager.flush()
        entityManager.clear()

        val loaded = adapter.findById(tour.id)

        assertEquals(tour.id, loaded?.id)
        assertEquals(tour.guideId, loaded?.guideId)
        assertEquals(tour.title, loaded?.title)
        assertEquals(tour.description, loaded?.description)
        assertEquals(tour.city, loaded?.city)
        assertEquals(tour.category, loaded?.category)
        assertEquals(tour.meetingPoint, loaded?.meetingPoint)
        assertEquals(tour.timezone, loaded?.timezone)
        assertEquals(tour.durationMinutes, loaded?.durationMinutes)
        assertEquals(0, tour.price!!.compareTo(loaded!!.price))
        assertEquals(tour.maxPeople, loaded.maxPeople)
        assertEquals(tour.status, loaded.status)
        assertEquals(tour.photos.map { it.url }, loaded.photos.map { it.url })
    }

    @Test
    fun `обновление тура не теряет фотографии`() {
        val guideId = persistedGuideId()
        val tour = tour(
            guideId,
            photos = listOf(
                TourPhoto(id = UUID.randomUUID(), url = "https://example.com/1.jpg", sortOrder = 0),
                TourPhoto(id = UUID.randomUUID(), url = "https://example.com/2.jpg", sortOrder = 1),
            ),
        )

        adapter.save(tour)
        entityManager.flush()
        entityManager.clear()

        val loaded = adapter.findById(tour.id)!!
        loaded.addPhoto("https://example.com/3.jpg")
        adapter.save(loaded)

        entityManager.flush()
        entityManager.clear()

        val loadedAfterUpdate = adapter.findById(tour.id)!!

        assertEquals(3, loadedAfterUpdate.photos.size)
        assertEquals(
            listOf("https://example.com/1.jpg", "https://example.com/2.jpg", "https://example.com/3.jpg"),
            loadedAfterUpdate.photos.sortedBy { it.sortOrder }.map { it.url },
        )
    }
}

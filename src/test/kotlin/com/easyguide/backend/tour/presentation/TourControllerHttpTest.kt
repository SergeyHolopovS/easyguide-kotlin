package com.easyguide.backend.tour.presentation

import com.easyguide.backend.TestcontainersConfiguration
import com.easyguide.backend.shared.application.port.TokenIssuer
import com.easyguide.backend.tour.domain.model.Tour
import com.easyguide.backend.tour.domain.model.TourCategory
import com.easyguide.backend.tour.domain.model.TourStatus
import com.easyguide.backend.tour.domain.repository.TourRepository
import com.easyguide.backend.user.domain.model.User
import com.easyguide.backend.user.domain.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
class TourControllerHttpTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tourRepository: TourRepository

    @Autowired
    private lateinit var tokenIssuer: TokenIssuer

    private fun guide(): User {
        val user = User(
            id = UUID.randomUUID(),
            name = "Гид",
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
        return userRepository.save(user)
    }

    private fun tour(guideId: UUID): Tour {
        val tour = Tour(
            id = UUID.randomUUID(),
            guideId = guideId,
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
        return tourRepository.save(tour)
    }

    private fun tokenFor(user: User): String = tokenIssuer.issue(user.id)

    private fun validTourBody(): String = """
        {
          "title": "Прогулка по центру города",
          "description": "Подробная экскурсия по историческому центру города с профессиональным гидом",
          "city": "Москва",
          "category": "WALKING",
          "meetingPoint": "Красная площадь",
          "timezone": "Europe/Moscow",
          "durationMinutes": 120,
          "price": 1500.00,
          "maxPeople": 10
        }
    """.trimIndent()

    private fun invalidTourBody(): String = """
        {
          "title": "abc",
          "description": "Подробная экскурсия по историческому центру города с профессиональным гидом",
          "city": "Москва",
          "category": "WALKING",
          "meetingPoint": "Красная площадь",
          "timezone": "Europe/Moscow",
          "durationMinutes": 120,
          "price": 1500.00,
          "maxPeople": 10
        }
    """.trimIndent()

    @Test
    fun `создание тура без токена возвращает 401`() {
        mockMvc.perform(
            post("/api/tours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validTourBody()),
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `обновление чужого тура возвращает 403 и код FORBIDDEN`() {
        val owner = guide()
        val intruder = guide()
        val existingTour = tour(owner.id)

        mockMvc.perform(
            put("/api/tours/${existingTour.id}")
                .header("Authorization", "Bearer ${tokenFor(intruder)}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validTourBody()),
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.status").value("FORBIDDEN"))
    }

    @Test
    fun `обновление несуществующего тура возвращает 404 и код NOT_FOUND`() {
        val user = guide()

        mockMvc.perform(
            put("/api/tours/${UUID.randomUUID()}")
                .header("Authorization", "Bearer ${tokenFor(user)}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validTourBody()),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value("NOT_FOUND"))
    }

    @Test
    fun `создание тура с невалидным телом возвращает 400 и код VALIDATION`() {
        val user = guide()

        mockMvc.perform(
            post("/api/tours")
                .header("Authorization", "Bearer ${tokenFor(user)}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTourBody()),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value("VALIDATION"))
    }
}

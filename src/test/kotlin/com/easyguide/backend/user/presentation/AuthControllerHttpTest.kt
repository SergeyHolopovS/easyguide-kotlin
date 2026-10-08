package com.easyguide.backend.user.presentation

import com.easyguide.backend.TestcontainersConfiguration
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import com.jayway.jsonpath.JsonPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
class AuthControllerHttpTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `ответ на регистрацию не содержит passwordHash`() {
        val body = """
            {
              "name": "Иван Иванов",
              "email": "ivan-${UUID.randomUUID()}@example.com",
              "password": "secret123"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body),
        )
            .andExpect(status().isOk)
            .andExpect(content().string(not(containsString("passwordHash"))))
            .andExpect(content().string(not(containsString("password_hash"))))
    }

    private fun registerAndGetRefreshToken(): String {
        val body = """
            {
              "name": "Иван Иванов",
              "email": "ivan-${UUID.randomUUID()}@example.com",
              "password": "secret123"
            }
        """.trimIndent()

        val response = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.refreshToken").isString)
            .andReturn().response.contentAsString

        return JsonPath.read(response, "$.refreshToken")
    }

    private fun refresh(refreshToken: String) = mockMvc.perform(
        post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"refreshToken": "$refreshToken"}"""),
    )

    @Test
    fun `refresh ротирует токен, а повторное использование старого даёт 401`() {
        val initial = registerAndGetRefreshToken()

        val rotated: String = JsonPath.read(
            refresh(initial)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.token").isString)
                .andReturn().response.contentAsString,
            "$.refreshToken",
        )

        refresh(initial).andExpect(status().isUnauthorized)
        // повторное использование отозвало и новый токен
        refresh(rotated).andExpect(status().isUnauthorized)
    }

    @Test
    fun `после logout refresh-токен перестаёт работать`() {
        val refreshToken = registerAndGetRefreshToken()

        mockMvc.perform(
            post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"refreshToken": "$refreshToken"}"""),
        ).andExpect(status().isNoContent)

        refresh(refreshToken).andExpect(status().isUnauthorized)
    }
}

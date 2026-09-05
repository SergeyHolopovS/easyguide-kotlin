package com.easyguide.backend.user.presentation

import com.easyguide.backend.TestcontainersConfiguration
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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
}

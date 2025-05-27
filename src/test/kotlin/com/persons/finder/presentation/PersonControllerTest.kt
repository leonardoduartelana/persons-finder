package com.persons.finder.presentation
import com.fasterxml.jackson.databind.ObjectMapper
import com.persons.finder.presentation.dto.CreatePersonRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

class PersonControllerTest {

    @SpringBootTest
    @AutoConfigureMockMvc
    class PersonControllerIntegrationTest {

        @Autowired
        private lateinit var mockMvc: MockMvc

        @Autowired
        private lateinit var objectMapper: ObjectMapper

        @Test
        fun `POST create person returns created person and status 201`() {
            val createRequest = CreatePersonRequest(name = "Bob Smith")

            mockMvc.post("/api/v1/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }
                .andExpect {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.data.name") { value("Bob Smith") }
                    jsonPath("$.data.id") { exists() }
                    jsonPath("$.transactionId") { exists() }
                }
        }
    }
}

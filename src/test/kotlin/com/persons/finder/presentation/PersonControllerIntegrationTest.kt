package com.persons.finder.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.persons.finder.data.Person
import com.persons.finder.data.repository.LocationRepository
import com.persons.finder.data.repository.PersonRepository
import com.persons.finder.presentation.dto.LocationRequestDto
import com.persons.finder.presentation.dto.PersonRequestDto
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @BeforeEach
    fun setup() {
        // Clear the database before each test
        locationRepository.deleteAll()
        personRepository.deleteAll()
    }

    @Test
    fun `should create a person`() {
        val personRequest = PersonRequestDto(name = "Foo Bar")

        mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Foo Bar"))
            .andExpect(jsonPath("$.id").isNumber)
    }

    @Test
    fun `should update a person's location`() {
        // Create a person first
        val person = personRepository.save(Person(name = "Foo Bar"))
        
        val locationRequest = LocationRequestDto(latitude = 40.7128, longitude = -74.0060)

        mockMvc.perform(
            put("/api/v1/persons/${person.id}/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationRequest))
        )
            .andExpect(status().isNoContent)

        locationRepository.findByReferenceId(person.id).let {
            assertTrue(it?.latitude == 40.7128)
            assertTrue(it?.longitude == -74.0060)
        }
    }

    @Test
    fun `should return 404 when updating location for non-existent person`() {
        val locationRequest = LocationRequestDto(latitude = 40.7128, longitude = -74.0060)

        mockMvc.perform(
            put("/api/v1/persons/999/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationRequest))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should get persons by IDs`() {
        // Create some persons
        val person1 = personRepository.save(Person(name = "Foo Bar"))
        val person2 = personRepository.save(Person(name = "Jane Doe"))

        mockMvc.perform(
            get("/api/v1/persons")
                .param("id", person1.id.toString())
                .param("id", person2.id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(person1.id))
            .andExpect(jsonPath("$[0].name").value("Foo Bar"))
            .andExpect(jsonPath("$[1].id").value(person2.id))
            .andExpect(jsonPath("$[1].name").value("Jane Doe"))
    }

    @Test
    fun `should return 404 when getting non-existent person`() {
        mockMvc.perform(
            get("/api/v1/persons")
                .param("id", "999")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should get nearby persons sorted by distance`() {
        // Create persons with locations
        val person1 = personRepository.save(Person(name = "Foo Bar"))
        val cordisLocation = LocationRequestDto(
            -36.857491, 174.763938     // Cordis Restaurant
        )

        val person2 = personRepository.save(Person(name = "Jane Doe"))
        val quayStreetLocation = LocationRequestDto(
            -36.842797, 174.765982      // Quay Street Waterfront
        )

        val person3 = personRepository.save(Person(name = "Bob Smith"))
        val motatMoseumLocation = LocationRequestDto(
            -36.867703, 174.727072     // MOTAT Museum
        )

        // Set locations
        mockMvc.perform(
            put("/api/v1/persons/${person1.id}/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cordisLocation))
        ).andExpect(status().isNoContent)

        mockMvc.perform(
            put("/api/v1/persons/${person2.id}/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quayStreetLocation))
        ).andExpect(status().isNoContent)

        mockMvc.perform(
            put("/api/v1/persons/${person3.id}/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motatMoseumLocation))
        ).andExpect(status().isNoContent)

        // Search for people within 2km of Britomart Auckland
        val britomartLocation = LocationRequestDto(
            -36.844044, 174.767208
        )

        mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("lat", britomartLocation.latitude.toString())
                .param("lon", britomartLocation.longitude.toString())
                .param("radiusKm", "2.0")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons.length()").value(2)) // Should find Cordis and Quay Street
            .andExpect(jsonPath("$.persons[*].latitude").exists())
            .andExpect(jsonPath("$.persons[*].longitude").exists())
            .andExpect(jsonPath("$.persons[*].distanceKm").exists())
            // Verify sorting - Quay Street should be first (closer to Britomart)
            .andExpect(jsonPath("$.persons[0].name").value("Jane Doe"))
            .andExpect(jsonPath("$.persons[0].latitude").value(quayStreetLocation.latitude))
            .andExpect(jsonPath("$.persons[0].longitude").value(quayStreetLocation.longitude))
            .andExpect(jsonPath("$.persons[1].name").value("Foo Bar"))
            .andExpect(jsonPath("$.persons[1].latitude").value(cordisLocation.latitude))
            .andExpect(jsonPath("$.persons[1].longitude").value(cordisLocation.longitude))
            // Verify distances are present
            .andExpect(jsonPath("$.persons[0].distanceKm").isNumber())
            .andExpect(jsonPath("$.persons[1].distanceKm").isNumber())
            .andDo { result ->
                val json: String? = result.getResponse().getContentAsString()
                val first = JsonPath.parse(json).read("$.persons[0].distanceKm", Double::class.java).toString()
                val second = JsonPath.parse(json).read("$.persons[1].distanceKm", Double::class.java).toString()
                assertTrue(first.toDouble() <= second.toDouble())
            }
    }

    @Test
    fun `should handle invalid radius in nearby search`() {
        mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("lat", "40.7128")
                .param("lon", "-74.0060")
                .param("radiusKm", "-1.0") // Invalid radius
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should handle invalid coordinates in nearby search`() {
        mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("lat", "91.0") // Invalid latitude
                .param("lon", "-74.0060")
                .param("radiusKm", "10.0")
        )
            .andExpect(status().isBadRequest)

        mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("lat", "40.7128")
                .param("lon", "-181.0") // Invalid longitude
                .param("radiusKm", "10.0")
        )
            .andExpect(status().isBadRequest)
    }
}
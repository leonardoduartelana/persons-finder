package com.persons.finder.performance

import com.persons.finder.data.Person
import com.persons.finder.data.repository.LocationRepository
import com.persons.finder.data.repository.PersonRepository
import com.persons.finder.presentation.dto.LocationRequestDto
import com.persons.finder.util.GeoUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NearbyPersonsPerformanceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var locationRepository: LocationRepository

    // Auckland region boundaries
    private val MIN_LAT = -37.0
    private val MAX_LAT = -36.7
    private val MIN_LON = 174.5
    private val MAX_LON = 175.0

    // Test locations
    private val britomartLocation = LocationRequestDto(-36.844044, 174.767208)
    private val NUM_RECORDS = 1_000

    @BeforeAll
    fun setup() {
        println("Starting data generation for $NUM_RECORDS records...")
        val startTime = System.currentTimeMillis()

        // Generate and save persons in batches
        val batchSize = 100
        repeat(NUM_RECORDS / batchSize) { batchIndex ->
            val persons = (1..batchSize).map { 
                Person(name = "Person ${batchIndex * batchSize + it}")
            }
            val savedPersons = personRepository.saveAll(persons)

            // Generate random locations for each person
            savedPersons.forEach { person ->
                val location = LocationRequestDto(
                    latitude = Random.nextDouble(MIN_LAT, MAX_LAT),
                    longitude = Random.nextDouble(MIN_LON, MAX_LON)
                )
                mockMvc.perform(
                    put("/api/v1/persons/${person.id}/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location))
                ).andExpect(status().isNoContent)
            }

            if ((batchIndex + 1) % 10 == 0) {
                println("Generated ${(batchIndex + 1) * batchSize} records...")
            }
        }

        val totalTime = System.currentTimeMillis() - startTime
        println("Data generation completed in ${totalTime / 1000} seconds")
    }

    @Test
    fun `benchmark nearby persons search with different radiuses`() {
        val radiuses = listOf(0.5, 1.0, 2.0, 5.0, 10.0)
        
        radiuses.forEach { radius ->
            val times = mutableListOf<Long>()
            
            // Warm-up run
            performNearbySearch(radius)
            
            // Actual benchmark runs
            repeat(5) {
                val time = measureTimeMillis {
                    val response = performNearbySearch(radius)
                    println("Found ${response.persons.size} results for radius ${radius}km")
                }
                times.add(time)
            }
            
            val avgTime = times.average()
            println("Average response time for radius ${radius}km: ${avgTime}ms")
        }
    }

    @Test
    fun `benchmark nearby persons search with different result sizes`() {
        // Test with different result sizes by adjusting the search area
        val testCases = listOf(
            Triple(0.5, "Small area", "~10-50 results"),
            Triple(2.0, "Medium area", "~100-500 results"),
            Triple(5.0, "Large area", "~1000-5000 results"),
            Triple(10.0, "Very large area", "~10000+ results")
        )

        testCases.forEach { (radius, description, expectedResults) ->
            println("\nTesting $description ($expectedResults)")
            val times = mutableListOf<Long>()
            
            // Warm-up run
            performNearbySearch(radius)
            
            // Actual benchmark runs
            repeat(5) {
                val time = measureTimeMillis {
                    val response = performNearbySearch(radius)
                    println("Found ${response.persons.size} results ")

                }
                times.add(time)
            }
            
            val avgTime = times.average()
            val minTime = times.minOrNull()
            val maxTime = times.maxOrNull()
            
            println("Results for $description:")
            println("Average time: ${avgTime}ms")
            println("Min time: ${minTime}ms")
            println("Max time: ${maxTime}ms")
        }
    }

    private fun performNearbySearch(radiusKm: Double): ApiResponse {
        val result = mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("lat", britomartLocation.latitude.toString())
                .param("lon", britomartLocation.longitude.toString())
                .param("radiusKm", radiusKm.toString())
        )
            .andExpect(status().isOk)
            .andReturn()
            
        return objectMapper.readValue(result.response.contentAsString, ApiResponse::class.java)
    }

    @AfterAll
    fun cleanup() {
        locationRepository.deleteAll()
        personRepository.deleteAll()
    }
}

data class PersonDetails(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double
)

data class ApiResponse(
    val persons: List<PersonDetails>
)

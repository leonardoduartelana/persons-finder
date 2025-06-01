package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.data.repository.LocationRepository
import com.persons.finder.data.repository.PersonRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.support.TransactionTemplate
import kotlin.math.abs
import kotlin.properties.Delegates

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocationsServiceTest {

    @Autowired
    private lateinit var locationsService: LocationsService

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    private val geometryFactory: GeometryFactory = GeometryFactory(PrecisionModel(), 4326)

    @BeforeAll
    fun setupStaticPersons() {
        // Clean the person repository once to ensure predictable IDs for static data
        personRepository.deleteAll()
        // Create Person entities that will be available for all tests in this class.

        personRepository.save(Person(name = "STATIC_PERSON_1"))
        personRepository.save(Person(name = "STATIC_PERSON_2"))
        personRepository.save(Person(name = "STATIC_PERSON_3"))
    }


    @BeforeEach
    fun setup() {
        // Clean up existing locations
        locationRepository.deleteAll();
    }

    @Test
    fun `should add and find location successfully`() {


        // Create a test location (Britomart Auckland)
        val location = Location(
            referenceId = 1L,
            geom = geometryFactory.createPoint(Coordinate(174.767208, -36.844044))
        )

        // Add the location
        locationsService.addLocation(location)

        // Find locations within a 10 km radius of Sky Tower
        val foundLocations = locationsService.findAround(-36.848465, 174.762210, 10.0)

        // Assert that the location was found
        assert(foundLocations.isNotEmpty()) { "Expected to find at least one location" }
        val foundLocation = foundLocations.first()
        assert(foundLocation.referenceId == 1L) { "Expected to find location with referenceId 1" }
        assert(abs(foundLocation.latitude - (-36.844044)) < 0.0001) { "Latitude should match" }
        assert(abs(foundLocation.longitude - 174.767208) < 0.0001) { "Longitude should match" }
    }

    @Test
    fun `should not find locations outside search radius`() {
        // Add two locations: Quay Street Waterfront and MOTAT Museum
        val quayStreet = Location(
            referenceId = 1L,
            geom = geometryFactory.createPoint(Coordinate(174.765982, -36.842797))
        )

        val motatMuseum = Location(
            referenceId = 2L,
            geom = geometryFactory.createPoint(Coordinate(174.727072, -36.867703))
        )

        locationsService.addLocation(quayStreet)
        locationsService.addLocation(motatMuseum)

        // Search with 2km radius around Britomart Auckland
        val foundLocations = locationsService.findAround(-36.844044, 174.767208, 2.0)

        // Should only find Quay Street Waterfront
        assert(foundLocations.size == 1) { "Expected to find only one location" }
        assert(foundLocations.first().referenceId == 1L) { "Expected to find only Quay Street Waterfront location" }
    }

    @Test
    fun `should remove location successfully`() {
        // Create and add a test location
        val location = Location(
            referenceId = 1L,
            geom = geometryFactory.createPoint(Coordinate(174.765982, -36.842797))
        )

        locationsService.addLocation(location)

        val existingLocation = locationsService.findAround(-36.842797, 174.765982, 2.0)
        assert(existingLocation.isNotEmpty()) { "Expected there is a location" }

        // Remove the location
        locationsService.removeLocation(1L)

        // Verify location was removed
        val foundLocations = locationsService.findAround(-36.842797, 174.765982, 2.0)
        assert(foundLocations.isEmpty()) { "Expected no locations after removal" }
    }

    @Test
    fun `should update existing location`() {
        // Create initial location Auckland Sky Tower
        val skyTowerLocation = Location(
            referenceId = 1L,
            geom = geometryFactory.createPoint(Coordinate(174.762327, -36.848320))
        )
        locationsService.addLocation(skyTowerLocation)

        // Update location (move to Britomart Auckland)
        val britomartLocation = Location(
            referenceId = 1L,
            geom = geometryFactory.createPoint(Coordinate(174.767208, -36.844044))
        )
        locationsService.addLocation(britomartLocation)

        val commercialBayLocation = Location(
            referenceId = 1L,
            geom = geometryFactory.createPoint(Coordinate(174.766122, -36.843934))
        )

        // Find updated location around Commercial Bay
        val foundLocations = locationsService.findAround(
            commercialBayLocation.latitude,
            commercialBayLocation.longitude,
            2.0
        )

        assert(foundLocations.isNotEmpty()) { "Expected to find updated location" }
        val foundLocation = foundLocations.first()
        assert(foundLocation.referenceId == 1L) { "Expected to find location with referenceId 1" }
        assert(abs(foundLocation.latitude - britomartLocation.latitude) < 0.0001) { "Updated latitude should match" }
        assert(abs(foundLocation.longitude - britomartLocation.longitude) < 0.0001) { "Updated longitude should match" }
    }
}
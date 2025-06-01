package com.persons.finder.presentation

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.presentation.dto.*
import com.persons.finder.presentation.exception.ResourceNotFoundException
import com.persons.finder.presentation.validation.ValidLatitude
import com.persons.finder.presentation.validation.ValidLongitude
import com.persons.finder.util.GeoUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("api/v1/persons")
@Tag(name = "Persons", description = "Persons API")
@Validated
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
    private val locationsService: LocationsService,
    private val geometryFactory: GeometryFactory
) {

    @Operation(summary = "Create a new person", description = "Creates a new person with the provided name")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Person created successfully"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @PostMapping
    fun createPerson(@Valid @RequestBody personRequest: PersonRequestDto): ResponseEntity<PersonResponseDto> {
        val person = Person(
            id = 0, // ID will be assigned by the service
            name = personRequest.name
        )
        personsService.save(person)

        val responseDto = PersonResponseDto(
            id = person.id,
            name = person.name
        )

        return ResponseEntity(responseDto, HttpStatus.CREATED)
    }

    @Operation(
        summary = "Update a person's location",
        description = "Updates or creates a person's location using latitude and longitude"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Location updated successfully"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Person not found",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @PutMapping("/{id}/location")
    fun updateLocation(
        @Parameter(description = "Person ID", required = true) @PathVariable id: Long,
        @Valid @RequestBody locationRequest: LocationRequestDto
    ): ResponseEntity<Unit> {

        personsService.getById(id) // Check if person exists

        // For simplicity, let's assume the DTO layer will handle Point creation.
        val point = geometryFactory.createPoint(Coordinate(locationRequest.longitude, locationRequest.latitude))

        val location = Location(
            id = 0, // ID will be assigned by the database
            referenceId = id,
            geom = point // Set the JTS Point
        )

        locationsService.addLocation(location)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Find nearby persons",
        description = "Finds persons around a query location within a specified radius, sorted by distance"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successfully retrieved list of nearby persons"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input parameters",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @GetMapping("/nearby")
    fun findNearbyPersons(
        @Parameter(description = "Latitude", required = true)
        @RequestParam
        @NotNull
        @ValidLatitude
        lat: Double,

        @Parameter(description = "Longitude", required = true)
        @RequestParam
        @NotNull
        @ValidLongitude
        lon: Double,

        @Parameter(description = "Radius in kilometers", required = true)
        @RequestParam
        @NotNull
        @Min(0)
        radiusKm: Double
    ): ResponseEntity<NearbyPersonsResponseDto> {

        val nearbyLocations = locationsService.findAround(lat, lon, radiusKm)

        val personIds = nearbyLocations.map { it.referenceId }

        val persons = if (personIds.isNotEmpty()) {
            personsService.getByIds(personIds)
        } else {
            emptyList()
        }
        // TODO optimize this to get location and person in one query

        val nearbyPersons = persons.mapNotNull { person ->
            val location = nearbyLocations.find { it.referenceId == person.id }
            location?.let {

                val distance = GeoUtils.calculateDistance(
                    lat1 = lat,
                    lon1 = lon,
                    lat2 = it.latitude,
                    lon2 = it.longitude
                )
                NearbyPersonDto(
                    id = person.id,
                    name = person.name,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    distanceKm = distance
                )
            }
        }.sortedBy { it.distanceKm }

        return ResponseEntity.ok(NearbyPersonsResponseDto(nearbyPersons))
    }

    @Operation(summary = "Get persons by IDs", description = "Retrieves one or more persons by their IDs")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successfully retrieved persons"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input parameters",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "One or more persons not found",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @GetMapping
    fun getPersonsByIds(
        @Parameter(description = "Person IDs", required = true) @RequestParam id: List<Long>
    ): ResponseEntity<List<PersonResponseDto>> {

        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val persons = personsService.getByIds(id)

        if (persons.size != id.size) {
            throw ResourceNotFoundException("One or more persons not found")
        }

        val personDtos = persons.map { person ->
            PersonResponseDto(
                id = person.id,
                name = person.name
            )
        }

        return ResponseEntity.ok(personDtos)
    }
}

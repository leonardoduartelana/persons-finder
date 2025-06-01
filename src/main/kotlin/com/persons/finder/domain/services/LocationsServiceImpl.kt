package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.repository.LocationRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationsServiceImpl @Autowired constructor(
    private val locationRepository: LocationRepository,
    // GeometryFactory is thread-safe and can be injected or created once.
    // SRID 4326 for WGS84
    private val geometryFactory: GeometryFactory = GeometryFactory(PrecisionModel(), 4326)
) : LocationsService {

    @Transactional
    override fun addLocation(location: Location) {

        // Check if a location already exists for this reference ID
        val existingLocation = locationRepository.findByReferenceId(location.referenceId)

        if (existingLocation != null) {
            locationRepository.delete(existingLocation)
        }

        // Save the new location (which includes the Point)
        locationRepository.save(location)
    }

    @Transactional
    override fun removeLocation(locationReferenceId: Long) {
        val location = locationRepository.findByReferenceId(locationReferenceId)
        location?.let {
            locationRepository.delete(it)
        }
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        // Validate radius
        if (radiusInKm < 0) {
            throw IllegalArgumentException("Radius must be non-negative")
        }

        val centerPoint: Point = geometryFactory.createPoint(Coordinate(longitude, latitude))
        val radiusInMeters = radiusInKm * 1000

        return locationRepository.findLocationsWithinDistance(centerPoint, radiusInMeters)
    }
}

package com.persons.finder.domain.services

import com.persons.finder.data.Location
import org.springframework.stereotype.Service

@Service
class LocationsServiceImpl : LocationsService {

    override fun addLocation(location: Location) {
        TODO("Not yet implemented")
    }

    override fun removeLocation(locationReferenceId: Long) {
        TODO("Not yet implemented")
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        TODO("Not yet implemented")
    }

}
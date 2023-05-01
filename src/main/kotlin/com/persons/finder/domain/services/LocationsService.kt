package com.persons.finder.domain.services

import com.persons.finder.data.Location

interface LocationsService {
    fun addLocation(location: Location)
    fun removeLocation(locationReferenceId: Long)
    fun findAround(latitude: Double, longitude: Double, radiusInKm: Double) : List<Location>
}
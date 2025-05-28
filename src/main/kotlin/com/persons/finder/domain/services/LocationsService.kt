package com.persons.finder.domain.services

import com.persons.finder.data.Location

interface LocationsService {
    fun addLocation(location: Location): Location
    fun removeLocation(personId: Long)
    fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location>
}

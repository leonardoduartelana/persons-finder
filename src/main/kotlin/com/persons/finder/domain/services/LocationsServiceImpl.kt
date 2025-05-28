package com.persons.finder.domain.services

import com.persons.finder.common.exception.PersonNotFoundException
import com.persons.finder.data.Location
import com.persons.finder.repository.LocationRepository
import com.persons.finder.repository.PersonRepository
import org.springframework.stereotype.Service

@Service
class LocationsServiceImpl(private val locationRepository: LocationRepository, private val personRepository: PersonRepository) : LocationsService {

    override fun addLocation(location: Location): Location {
        val person = personRepository.findById(location.personId)
        if (person.isEmpty) throw PersonNotFoundException("No persons found for given ID")
        return locationRepository.save(location)
    }

    override fun removeLocation(locationReferenceId: Long) {
        TODO("Not yet implemented")
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        TODO("Not yet implemented")
    }
}

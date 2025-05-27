package com.persons.finder.presentation.controller

import com.persons.finder.data.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.api.PersonApi
import com.persons.finder.presentation.dto.CreatePersonRequest
import com.persons.finder.presentation.dto.CreatePersonResponse
import com.persons.finder.presentation.dto.GetPersonResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class PersonController(private val personService: PersonsService) : PersonApi {

    /*
        TODO PUT API to update/create someone's location using latitude and longitude
        (JSON) Body
     */

    /*
        TODO POST API to create a 'person'
        (JSON) Body and return the id of the created entity
    */
    override fun createPerson(
        @Valid @RequestBody
        request: CreatePersonRequest,
    ): ResponseEntity<CreatePersonResponse> {
        val person = Person(name = request.name)
        val savedPerson = personService.save(person)
        val response = CreatePersonResponse(data = savedPerson)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /*
        TODO GET API to retrieve people around query location with a radius in KM, Use query param for radius.
        TODO API just return a list of persons ids (JSON)
        // Example
        // John wants to know who is around his location within a radius of 10km
        // API would be called using John's id and a radius 10km
     */

    /*
        TODO GET API to retrieve a person or persons name using their ids
        // Example
        // John has the list of people around them, now they need to retrieve everybody's names to display in the app
        // API would be called using person or persons ids
     */

    override fun getPersonsById(ids: List<Long>): ResponseEntity<GetPersonResponse> {
        val persons = personService.getByIds(ids)

        return if (persons.isEmpty()) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(GetPersonResponse(data = persons))
        }
    }
}

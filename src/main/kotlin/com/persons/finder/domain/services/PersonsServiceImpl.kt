package com.persons.finder.domain.services

import com.persons.finder.data.Person
import com.persons.finder.repository.PersonRepository
import org.springframework.stereotype.Service

@Service
class PersonsServiceImpl(private val personRepository: PersonRepository) : PersonsService {

    override fun getByIds(id: List<Long>): List<Person> {
        return personRepository.findAllByIdIn(id)
    }

    override fun save(person: Person): Person {
        return personRepository.save(person)
    }
}

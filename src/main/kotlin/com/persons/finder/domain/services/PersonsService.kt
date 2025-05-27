package com.persons.finder.domain.services

import com.persons.finder.data.Person

interface PersonsService {
    fun getByIds(id: List<Long>): List<Person>
    fun save(person: Person): Person
}

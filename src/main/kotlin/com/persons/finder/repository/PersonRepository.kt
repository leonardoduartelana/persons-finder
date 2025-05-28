package com.persons.finder.repository

import com.persons.finder.data.Person
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<Person, Long> {
    fun findAllByIdIn(ids: List<Long>): List<Person>
}

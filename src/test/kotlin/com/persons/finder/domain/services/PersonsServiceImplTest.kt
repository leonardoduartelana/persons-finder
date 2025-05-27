package com.persons.finder.domain.services

import com.persons.finder.data.Person
import com.persons.finder.repository.PersonRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PersonsServiceImplTest {
    private val repository = mock<PersonRepository>()
    private val service = PersonsServiceImpl(repository)

    @Test
    fun `should save person successfully`() {
        val person = Person(id = 1L, name = "Alice")
        whenever(repository.save(person)).thenReturn(person)

        val saved = service.save(person)

        verify(repository).save(person)
        Assertions.assertEquals(person, saved)
    }
}

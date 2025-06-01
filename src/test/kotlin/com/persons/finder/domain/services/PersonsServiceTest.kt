package com.persons.finder.domain.services

import com.persons.finder.data.Person
import com.persons.finder.data.repository.PersonRepository
import com.persons.finder.presentation.exception.ResourceNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PersonsServiceTest {

    @Mock
    private lateinit var personRepository: PersonRepository

    @InjectMocks
    private lateinit var personsService: PersonsServiceImpl

    @Test
    fun `getById should return person when found`() {
        // Arrange
        val personId = 1L
        val expectedPerson = Person(id = personId, name = "John Doe")
        Mockito.`when`(personRepository.findById(personId))
            .thenReturn(Optional.of(expectedPerson))

        // Act
        val result = personsService.getById(personId)

        // Assert
        assertEquals(expectedPerson, result)
        Mockito.verify(personRepository, Mockito.times(1)).findById(personId)
    }

    @Test
    fun `getById should throw ResourceNotFoundException when person not found`() {
        // Arrange
        val personId = 1L
        Mockito.`when`(personRepository.findById(personId)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            personsService.getById(personId)
        }
        assertEquals("Person with ID $personId not found", exception.message)
        Mockito.verify(personRepository, Mockito.times(1)).findById(personId)
    }

    @Test
    fun `save should call repository save method`() {
        // Arrange
        val person = Person(id = 0, name = "John Doe")
        val savedPerson = Person(id = 1, name = "John Doe")
        Mockito.`when`(personRepository.save(person)).thenReturn(savedPerson)

        // Act
        personsService.save(person)

        // Assert
        Mockito.verify(personRepository, Mockito.times(1)).save(person)
    }
}
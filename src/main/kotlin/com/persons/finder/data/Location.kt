package com.persons.finder.data

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Location(
    @Id
    val personId: Long = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

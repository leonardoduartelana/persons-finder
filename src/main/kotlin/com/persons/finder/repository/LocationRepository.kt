package com.persons.finder.repository

import com.persons.finder.data.Location
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository : JpaRepository<Location, Long>

package com.persons.finder.data.repository

import com.persons.finder.data.Location
import org.locationtech.jts.geom.Point // Import JTS Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Long> {
    
    fun findByReferenceId(referenceId: Long): Location?


    // Native query for ST_DWithin with distance in meters.
    // Note: :point must be a WKT string or a JTS Point object that Hibernate Spatial can convert.
    // Spring Data JPA will handle the Point object correctly.

    @Query(value = """
        SELECT * FROM locations l 
        WHERE ST_DWithin(l.geom\:\:geography, ST_SetSRID(ST_MakePoint(:#{#point.x}, :#{#point.y}), 4326)\:\:geography, :radiusInMeters)
    """, nativeQuery = true)
    fun findLocationsWithinDistance(
        @Param("point") point: Point,
        @Param("radiusInMeters") radiusInMeters: Double
    ): List<Location>

}
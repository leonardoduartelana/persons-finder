package com.persons.finder.data

import org.locationtech.jts.geom.Point // Import JTS Point
import javax.persistence.*

@Entity
@Table(name = "locations")
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // Person's id is used for this field
    @Column(nullable = false)
    val referenceId: Long,


    @Column(columnDefinition = "GEOMETRY(Point, 4326)", nullable = false)
    val geom: Point) {

        val latitude: Double
            get() = geom.y

        val longitude: Double
            get() = geom.x
    }



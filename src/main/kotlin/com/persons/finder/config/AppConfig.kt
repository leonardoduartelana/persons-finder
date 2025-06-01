package com.persons.finder.config

import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun mySingletonService(): GeometryFactory {
        return GeometryFactory(PrecisionModel(), 4326)
    }
}
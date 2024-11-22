package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.City
import com.govzcode.sportevents.entity.Region
import org.springframework.data.jpa.repository.JpaRepository

interface CityRepository : JpaRepository<City, Long> {
    fun findByNameAndRegion(name: String, region: Region): City?
}

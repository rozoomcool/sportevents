package com.govzcode.sportevents.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "county")
class Country(
    @Column(name = "name", nullable = true, unique = false)
    var name: String
): BaseEntity<Long>() {
}
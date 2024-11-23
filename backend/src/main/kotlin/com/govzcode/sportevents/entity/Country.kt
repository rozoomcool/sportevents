package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "country")
class Country(
        @Column(name = "name", nullable = false, unique = true)
        var name: String
) : BaseEntity<Long>()

package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "country")
class Country(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @OneToMany(mappedBy = "country", cascade = [CascadeType.ALL], orphanRemoval = true)
        @JsonManagedReference
        var regions: MutableList<Region> = mutableListOf()
) : BaseEntity<Long>()

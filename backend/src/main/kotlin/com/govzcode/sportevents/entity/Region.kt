package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "region")
class Region(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "country_id", nullable = false)
        @JsonBackReference
        var country: Country,

        @OneToMany(mappedBy = "region", cascade = [CascadeType.ALL], orphanRemoval = true)
        @JsonManagedReference
        var cities: MutableList<City> = mutableListOf()
) : BaseEntity<Long>()

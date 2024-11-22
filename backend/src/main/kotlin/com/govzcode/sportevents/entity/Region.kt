package com.govzcode.sportevents.entity

import jakarta.persistence.*

@Entity
@Table(name = "region")
class Region(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "country_id", nullable = false)
        var country: Country,

        @OneToMany(mappedBy = "region", cascade = [CascadeType.ALL], orphanRemoval = true)
        var cities: MutableList<City> = mutableListOf()
) : BaseEntity<Long>()

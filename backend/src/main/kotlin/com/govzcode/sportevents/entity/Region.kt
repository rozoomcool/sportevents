package com.govzcode.sportevents.entity

import jakarta.persistence.*

@Entity
@Table(name = "region")
class Region(
        @Column(name = "region", nullable = false, unique = true)
        var name: String,
        @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "region_id", referencedColumnName = "id")
        var city: City
) : BaseEntity<Long>() {
}
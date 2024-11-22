package com.govzcode.sportevents.entity

import jakarta.persistence.*

@Entity
@Table(name = "county")
class Country(
    @Column(name = "name", nullable = true, unique = false)
    var name: String,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    var region: Region
): BaseEntity<Long>() {
}
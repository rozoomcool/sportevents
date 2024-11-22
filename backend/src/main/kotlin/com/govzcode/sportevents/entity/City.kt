package com.govzcode.sportevents.entity

import jakarta.persistence.*

@Entity
@Table(name = "city")
class City(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "region_id", nullable = false)
        var region: Region
) : BaseEntity<Long>()

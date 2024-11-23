package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "city")
class City(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "region_id", nullable = false)
        @JsonBackReference
        var region: Region
) : BaseEntity<Long>()

package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "region")
class Region(
        @Column(name = "name", nullable = false, unique = true)
        var name: String,

        @ManyToMany(mappedBy = "regions")
        @JsonBackReference
        val sportEvents: Set<SportEvent> = hashSetOf()
) : BaseEntity<Long>()

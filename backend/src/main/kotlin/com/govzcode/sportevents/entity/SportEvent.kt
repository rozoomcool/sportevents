package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "event")
class SportEvent(
    @Column(name = "ekp_id", nullable = false, unique = true)
    var ekpId: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_auditory_id", nullable = false)
    var targetAuditory: TargetAuditory,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_discipline",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "discipline_id")]
    )
    @JsonManagedReference
    var disciplines: MutableList<Discipline>,

    @Column(name = "title", nullable = false)
    var title: String,

    var startsDate: Date,
    var endsDate: Date,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    var country: Country,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_region",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "region_id")]
    )
    @JsonManagedReference
    val regions: Set<Region> = hashSetOf(),

    var numberOfParticipant: Long
) : BaseAuditEntity<Long>()

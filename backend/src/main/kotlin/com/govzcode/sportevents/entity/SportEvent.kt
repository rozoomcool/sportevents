package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "event")
class SportEvent(
    @Column(name = "ekp_id", nullable = false, unique = true)
    var ekpId: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_target_auditory",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "target_auditory_id")]
    )
    @JsonManagedReference
    var targetAuditory: MutableList<TargetAuditory>,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "sport_title", nullable = false)
    var sportTitle: String,

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

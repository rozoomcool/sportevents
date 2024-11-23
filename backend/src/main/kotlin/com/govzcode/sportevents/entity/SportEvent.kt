package com.govzcode.sportevents.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "sport_event")
class SportEvent(
        @Column(name = "ekp_id", nullable = false, unique = true)
        var ekpId: String,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "target_auditory_id", nullable = false)
        var targetAuditory: TargetAuditory,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "discipline_id", nullable = false)
        var discipline: Discipline,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "program_id", nullable = false)
        var program: Program,

        var startsDate: Date,
        var endsDate: Date,

//        @ManyToOne(fetch = FetchType.EAGER)
//        @JoinColumn(name = "country_id", nullable = false)
//        var country: Country,
//
//        @ManyToOne(fetch = FetchType.EAGER)
//        @JoinColumn(name = "region_id", nullable = false)
//        var region: Region,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "city_id", nullable = false)
        var city: City,

        var numberOfParticipant: Long
) : BaseAuditEntity<Long>()

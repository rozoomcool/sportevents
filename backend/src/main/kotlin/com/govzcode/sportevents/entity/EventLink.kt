package com.govzcode.sportevents.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "event_link")
class EventLink(
    @Column(name = "title", nullable = false, unique = true)
    var title: String,
    @Column(name = "link", nullable = false)
    var link: String
): BaseEntity<Long>() {
}
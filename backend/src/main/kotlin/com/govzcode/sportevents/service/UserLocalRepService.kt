package com.govzcode.sportevents.service

import org.springframework.stereotype.Service

@Service
class UserLocalRepService {
    private val users: MutableSet<String> = mutableSetOf()

    fun addUser(username: String) {
        users.add(username)
    }

    fun getAll(): List<String> = users.toList()
}
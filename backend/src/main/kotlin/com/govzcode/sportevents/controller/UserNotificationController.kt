package com.govzcode.sportevents.controller

import com.govzcode.sportevents.model.CustomPrincipal
import com.govzcode.sportevents.service.UserLocalRepService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/notif")
@Tag(name="notifications")
class UserNotificationController(
    private val userLocalRepService: UserLocalRepService
) {

    @PostMapping
    fun subscribe(principal: CustomPrincipal) {
        userLocalRepService.addUser(principal.username)
    }

}
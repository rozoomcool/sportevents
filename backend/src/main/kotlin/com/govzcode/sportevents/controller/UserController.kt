package com.govzcode.sportevents.controller

import com.govzcode.sportevents.entity.User
import com.govzcode.sportevents.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("api/v1/user")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/all")
    fun getAll(pageable: Pageable, assembler: PagedResourcesAssembler<User>): PagedModel<EntityModel<User>> =
        assembler.toModel(userService.getPage())

    @GetMapping()
    fun me(principal: Principal): User = userService.findByUsername(principal.name)
}
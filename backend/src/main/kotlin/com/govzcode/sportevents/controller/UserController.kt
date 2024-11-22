package com.govzcode.sportevents.controller

import com.govzcode.sportevents.dto.PageableDto
import com.govzcode.sportevents.entity.User
import com.govzcode.sportevents.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("api/v1/user")
@Tag(name = "user")
class UserController(
        private val userService: UserService
) {
    @GetMapping("/all")
    fun getAll(
            @RequestParam(defaultValue = "10") size: Int,
            @RequestParam(defaultValue = "1") page: Int,
            @RequestParam sort: List<String>?
    ): PageableDto<User> {
        val sortCriteria = sort?.fold(Sort.unsorted()) { acc, sortParam ->
            val parts = sortParam.split(":")
            val field = parts[0]
            val direction = if (parts.getOrNull(1)?.equals("desc", true) == true) {
                Sort.Direction.DESC
            } else {
                Sort.Direction.ASC
            }
            acc.and(Sort.by(direction, field))
        } ?: Sort.unsorted()
        val entity = userService.getPage(PageRequest.of(page, size))
        return PageableDto(
                content = entity.content,
                totalElements = entity.totalElements,
                page = entity.number,
                size = entity.size,
                totalPages = entity.count()

        )
    }


    @GetMapping()
    fun me(principal: Principal): User = userService.findByUsername(principal.name)
}
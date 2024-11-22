package com.govzcode.sportevents.dto

data class PageableDto<T>(
        val content: List<T>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int
)
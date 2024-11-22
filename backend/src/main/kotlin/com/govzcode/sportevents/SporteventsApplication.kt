package com.govzcode.sportevents

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
//@Import(RepositoryRestMvcConfiguration::class)
class SporteventsApplication

fun main(args: Array<String>) {
	runApplication<SporteventsApplication>(*args)
}

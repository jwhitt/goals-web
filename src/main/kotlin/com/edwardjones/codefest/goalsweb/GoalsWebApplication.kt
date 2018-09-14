package com.edwardjones.codefest.goalsweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GoalsWebApplication

fun main(args: Array<String>) {
    runApplication<GoalsWebApplication>(*args)
}

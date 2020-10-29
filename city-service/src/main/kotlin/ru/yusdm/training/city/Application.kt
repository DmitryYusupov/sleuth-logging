package ru.yusdm.training.city

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["ru.yusdm.training.city", "ru.yusdm.training.common.sleuth"])
//@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

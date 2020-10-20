package ru.yusdm.training.sleuth.logging.service

import org.springframework.stereotype.Service
import ru.yusdm.training.sleuth.logging.model.User
import ru.yusdm.training.sleuth.logging.utils.logger

private val log = UserService::class.logger
@Service
class UserService {

    fun getAllUsers(): List<User> {

        log.info("Service: getAllUsers")
        return listOf(
            User("Dmitry"),
            User("Darya"),
            User("Paul"),
            User("Ted")
        )
    }
}
package ru.yusdm.training.sleuth.logging.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import ru.yusdm.training.sleuth.logging.model.User
import ru.yusdm.training.sleuth.logging.service.OrderService
import ru.yusdm.training.sleuth.logging.service.UserService
import ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage
import ru.yusdm.training.sleuth.logging.utils.logger
import ru.yusdm.training.sleuth.logging.sleuth.setBaggageField

private val log = UserController::class.logger
const val USER_ID = "userid"

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val orderService: OrderService
) {

    @PostMapping
    //#{@myConfig.interval}
    @CreateBaggage(key = "userid", value = "#{user.name}", excludeFromMdcAfterMethodExit = false)
    fun postUser(@RequestBody user: User) {
        orderService.checkOrders()
    }

    @GetMapping(value = ["/1"])
    fun getAllUsers(): List<User> {
        setBaggageField(USER_ID, "TestUser")
        orderService.checkOrders()
        return userService.getAllUsers()

    }

    @GetMapping(value = ["/2"])
    fun getAllUsers2(): List<User> {
        orderService.checkOrders()
        return userService.getAllUsers()

    }
}
package ru.yusdm.training.sleuth.logging.api

import org.springframework.web.bind.annotation.*
import ru.yusdm.training.sleuth.logging.model.User
import ru.yusdm.training.sleuth.logging.service.OrderService
import ru.yusdm.training.sleuth.logging.service.UserService
import ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage
import ru.yusdm.training.sleuth.logging.sleuth.setBaggageField
import ru.yusdm.training.sleuth.logging.utils.logger

private val log = UserController::class.logger
const val USER_ID = "userid"

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val orderService: OrderService
) {

    @PostMapping(value = ["post_1"])
    fun postUser_1(@RequestBody
                   user: User) {
        orderService.checkOrders(user)
    }

    @PostMapping(value = ["post_2"])
    @CreateBaggage(key = "userid", value = "@dataHolderBean.getData()")
    fun postUser_2(@RequestBody user: User) {
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
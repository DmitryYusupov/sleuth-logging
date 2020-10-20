package ru.yusdm.training.sleuth.logging.service

import org.springframework.stereotype.Service
import ru.yusdm.training.sleuth.logging.api.USER_ID
import ru.yusdm.training.sleuth.logging.sleuth.getBaggageField
import ru.yusdm.training.sleuth.logging.utils.logger

private val log = OrderService::class.logger
@Service
class OrderService {

    fun checkOrders() {
        log.info(getBaggageField(USER_ID))
        log.info("Order service")
    }
}
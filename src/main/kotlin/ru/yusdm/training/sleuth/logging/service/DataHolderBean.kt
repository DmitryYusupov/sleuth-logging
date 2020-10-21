package ru.yusdm.training.sleuth.logging.service

import org.springframework.stereotype.Component

@Component
class DataHolderBean {

    fun getData(): String {
        return "Test333"
    }
}
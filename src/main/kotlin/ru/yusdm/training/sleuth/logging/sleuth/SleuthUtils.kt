package ru.yusdm.training.sleuth.logging.sleuth

import brave.baggage.BaggageField
import org.slf4j.MDC

fun setBaggageField(key: String, value: String){
    BaggageField.create(key).also {
        val wasUpdated = it.updateValue(value)
        if (wasUpdated){
            MDC.put(key, value)
        }
    }
}

fun getBaggageField(key: String): String {
    return BaggageField.getByName(key)?.value ?: ""
}
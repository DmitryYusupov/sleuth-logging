package ru.yusdm.training.country.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.yusdm.training.common.logger
import ru.yusdm.training.common.sleuth.SleuthBaggageFieldMaintainer.getBaggageField
import ru.yusdm.training.common.sleuth.SleuthBaggageFieldMaintainer.setBaggageField
import ru.yusdm.training.country.model.Country
import ru.yusdm.training.country.service.CountryService
import java.util.concurrent.atomic.AtomicInteger

private val log = CountryController::class.logger

const val USER_ID = "userid"

@RestController
@RequestMapping("/api/countries")
class CountryController(private val countryService: CountryService) {

    private val counter = AtomicInteger(0)

    @GetMapping(value = ["/{countryId}"])
    fun getCountryById(@PathVariable countryId: Long): ResponseEntity<Country> {
        setBaggageField(USER_ID, "TestUser ${counter.incrementAndGet()}")
        log.info("User id is " + getBaggageField(USER_ID))

        return countryService.findByCountryId(countryId = countryId)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

}
package ru.yusdm.training.country.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.yusdm.training.country.model.Country
import ru.yusdm.training.country.service.CountryService

@RestController
@RequestMapping("/api/countries")
class CountryController(private val countryService: CountryService) {

    @GetMapping(value = ["/{countryId}"])
    fun getCountryById(@PathVariable countryId: Long): ResponseEntity<Country> {
        return countryService.findByCountryId(countryId = countryId)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

}
package ru.yusdm.training.city.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.yusdm.training.city.model.City
import ru.yusdm.training.city.service.CityService

@RestController
@RequestMapping("/api/cities")
class CityController(private val cityService: CityService) {

    @GetMapping
    fun getCitiesByCountry(@RequestParam countryId: Long): ResponseEntity<List<City>> {
        return ResponseEntity.ok(cityService.getCitiesByCountry(countryId))
    }
}
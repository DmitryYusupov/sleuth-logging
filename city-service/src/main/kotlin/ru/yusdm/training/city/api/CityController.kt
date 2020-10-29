package ru.yusdm.training.city.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.yusdm.training.city.model.City
import ru.yusdm.training.city.service.CityService

@RestController
@RequestMapping("/api/cities")
class CityController(private val cityService: CityService) {

    @GetMapping
    fun getCitiesByCountry(@RequestParam countryId: Long): ResponseEntity<List<City>> {
        val result =  ResponseEntity.ok(cityService.getCitiesByCountry(countryId))
        log.info("Before response in cities")
        return result
    }

    @GetMapping("/{cityId}")
    fun getCityDetails(@PathVariable cityId: Long) : City {
        val city = cityService.getById(cityId)
        cityService.logFirstCityDetails(city)
        return city
    }
}
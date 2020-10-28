package ru.yusdm.training.city.service

import org.springframework.stereotype.Service
import ru.yusdm.training.city.model.City
import ru.yusdm.training.common.logger

val log = CityService::class.logger

@Service
class CityService {
    private val citiesByCountry = mapOf(
        1L to listOf(City(name = "Spb", mainStreet = "Nevsky"), City(name = "Msc", mainStreet = "Red square")),
        2L to listOf(
            City(name = "City_1", mainStreet = "City_1_main_steet"),
            City(name = "City_2", mainStreet = "City_2_main_steet")
        )
    )

    fun getCitiesByCountry(countryId: Long): List<City> {
        log.info("Get cities by country id {}", countryId)
        return citiesByCountry[countryId] ?: emptyList()
    }
}
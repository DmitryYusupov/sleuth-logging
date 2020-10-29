package ru.yusdm.training.city.service

import org.springframework.stereotype.Service
import ru.yusdm.training.city.model.City
import ru.yusdm.training.common.logger
import ru.yusdm.training.common.sleuth.CreateBaggage
import ru.yusdm.training.common.sleuth.Person

val log = CityService::class.logger

@Service
class CityService(val person: Person) {
    private val citiesByCountry = mapOf(
        1L to listOf(City(name = "Spb", mainStreet = "Nevsky"), City(name = "Msc", mainStreet = "Red square")),
        2L to listOf(
            City(name = "City_1", mainStreet = "City_1_main_steet"),
            City(name = "City_2", mainStreet = "City_2_main_steet")
        )
    )

    fun getCitiesByCountry(countryId: Long): List<City> {
        log.info("Get cities by country id {}", countryId)
        val result = citiesByCountry[countryId] ?: emptyList()
        if (result.isNotEmpty()) {
            logFirstCityDetails(result.first());
        }
        return result;
    }

    @CreateBaggage(key = "aa", value = "SS")
    fun logFirstCityDetails(city: City) {
        log.info("City details {}", city);
    }

   /* fun logFirstCityDetails(@CreateBaggage(key = "cityName", value = "#city.name88") city: City) {
        log.info("City details {}", city);
    }*/

    fun getById(cityId: Long): City {
        return City("City_$cityId", "MainStreet_$cityId")
    }

}
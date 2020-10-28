package ru.yusdm.training.country.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.yusdm.training.city.model.City
import ru.yusdm.training.common.logger
import ru.yusdm.training.common.sleuth.SleuthBaggageFieldMaintainer.getBaggageField
import ru.yusdm.training.country.api.USER_ID
import ru.yusdm.training.country.model.Country
import java.net.URI

val log = CountryService::class.logger

@Service
class CountryService(private val restTemplate: RestTemplate) {

    private val countriesById = mapOf(
        1L to Country(1L, "Russia", 1000, mutableListOf()),
        2L to Country(2L, "Country_2", 500, mutableListOf())
    )

    fun findByCountryId(countryId: Long): Country? {

        log.info("User id in (findByCountryId) is " + getBaggageField(USER_ID))

        return countriesById[countryId]?.let {
            val response = restTemplate.exchange(
                RequestEntity<Any>(HttpMethod.GET, URI.create("http://127.0.0.1:8081/api/cities?countryId=${it.id}")),
                object : ParameterizedTypeReference<List<City>>() {}
            )
            it.cities.addAll(response.body ?: emptyList())

            it
        }
    }

}
package ru.yusdm.training.country.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.yusdm.training.city.model.City
import ru.yusdm.training.country.model.Country
import java.net.URI

@Service
class CountryService(private val restTemplate: RestTemplate) {

    private val countriesById = mapOf(
        1L to Country(1L, "Russia", 1000, mutableListOf()),
        2L to Country(2L, "Country_2", 500, mutableListOf())
    )

    fun findByCountryId(countryId: Long): Country? {
        return countriesById[countryId]?.let {
            val response = restTemplate.exchange(
                RequestEntity<Any>(HttpMethod.GET, URI.create("http://127.0.0.1:8888/jedi.json")),
                object : ParameterizedTypeReference<List<City>>() {}
            )
            it.cities.addAll(response.body ?: emptyList())

            it
        }
    }

}
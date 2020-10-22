package ru.yusdm.training.country.model

import ru.yusdm.training.city.model.City

data class Country(
    val id: Long,
    val name: String,
    val population: Int,
    val cities: MutableList<City>
)

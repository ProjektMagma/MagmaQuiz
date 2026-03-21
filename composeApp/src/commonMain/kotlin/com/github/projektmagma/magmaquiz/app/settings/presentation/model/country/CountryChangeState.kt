package com.github.projektmagma.magmaquiz.app.settings.presentation.model.country

import com.github.projektmagma.magmaquiz.app.settings.domain.model.Country

data class CountryChangeState(
    val userTown: String = "",
    val countryName: String = "",
    val countriesList: List<Country> = emptyList(),
    val filteredCountriesList: List<Country> = emptyList(),
    val selectedCountry: Country? = null
)
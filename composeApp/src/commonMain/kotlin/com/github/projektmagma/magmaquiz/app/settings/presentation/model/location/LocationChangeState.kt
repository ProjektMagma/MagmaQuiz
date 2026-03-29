package com.github.projektmagma.magmaquiz.app.settings.presentation.model.location

import com.github.projektmagma.magmaquiz.app.settings.domain.model.Country

data class LocationChangeState(
    val userTown: String = "",
    val countryName: String = "",
    val countriesList: List<Country> = emptyList(),
    val filteredCountriesList: List<Country> = emptyList(),
    val selectedCountry: Country? = null
)
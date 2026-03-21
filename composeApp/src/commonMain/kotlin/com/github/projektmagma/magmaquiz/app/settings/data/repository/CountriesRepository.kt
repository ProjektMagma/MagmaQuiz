package com.github.projektmagma.magmaquiz.app.settings.data.repository

import com.github.projektmagma.magmaquiz.app.core.data.database.CountryDao
import com.github.projektmagma.magmaquiz.app.settings.domain.mappers.toModel
import com.github.projektmagma.magmaquiz.app.settings.domain.model.Country

class CountriesRepository(
    private val countryDao: CountryDao
) { 
    suspend fun getAllCountries(): List<Country> {
        return countryDao.getAllCountries().map { it.toModel() }
    }
    
    suspend fun getCountryByCode(code: String): Country{
        return countryDao.getCountryByCode(code).toModel()
    }
}
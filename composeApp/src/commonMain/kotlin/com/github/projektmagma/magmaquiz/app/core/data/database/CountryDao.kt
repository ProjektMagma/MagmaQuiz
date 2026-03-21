package com.github.projektmagma.magmaquiz.app.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CountryDao { 
    @Query("SELECT * FROM COUNTRYENTITY")
    suspend fun getAllCountries(): List<CountryEntity>
    
    @Query("SELECT * FROM COUNTRYENTITY WHERE code LIKE '%' || :code || '%' LIMIT 1")
    suspend fun getCountryByCode(code: String): CountryEntity
    
    @Insert
    suspend fun insertAll(countries: List<CountryEntity>)
}
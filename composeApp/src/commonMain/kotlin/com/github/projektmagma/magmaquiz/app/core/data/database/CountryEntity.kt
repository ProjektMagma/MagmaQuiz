package com.github.projektmagma.magmaquiz.app.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val flag: String
)
package com.github.projektmagma.magmaquiz.app.settings.domain.mappers

import com.github.projektmagma.magmaquiz.app.core.data.database.CountryEntity
import com.github.projektmagma.magmaquiz.app.settings.domain.localizedCountryName
import com.github.projektmagma.magmaquiz.app.settings.domain.model.Country

fun CountryEntity.toModel(): Country{
    return Country(
        name = localizedCountryName(this.code, this.name),
        code = this.code,
        flag = this.flag
    )
}
package com.github.projektmagma.magmaquiz.app.settings.presentation.model.country

import com.github.projektmagma.magmaquiz.app.settings.domain.model.Country

sealed interface LocationDetailsChangeCommand {
    data class UserTownChanged(val newTown: String): LocationDetailsChangeCommand
    data class CountryNameChanged(val newName: String): LocationDetailsChangeCommand
    data class CountrySelected(val country: Country): LocationDetailsChangeCommand
    data object Save: LocationDetailsChangeCommand
}
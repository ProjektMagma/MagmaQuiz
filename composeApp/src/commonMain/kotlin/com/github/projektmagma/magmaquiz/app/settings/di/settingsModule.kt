package com.github.projektmagma.magmaquiz.app.settings.di

import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.data.service.SettingsService
import com.github.projektmagma.magmaquiz.app.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    singleOf(::SettingsService)
    singleOf(::SettingsRepository)
    viewModelOf(::SettingsViewModel)
}
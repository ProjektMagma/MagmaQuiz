package com.github.projektmagma.magmaquiz.app.settings.di

import com.github.projektmagma.magmaquiz.app.core.data.database.getCountryDao
import com.github.projektmagma.magmaquiz.app.settings.data.repository.CountriesRepository
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.data.service.SettingsService
import com.github.projektmagma.magmaquiz.app.settings.presentation.SettingsViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.account.AccountDetailsChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.change.EmailChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.email.verify.EmailVerifyViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.location.LocationDetailsChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.change.PasswordChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.entry.PasswordEmailEntryViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.screens.password.verify.PasswordVerifyViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    singleOf(::SettingsService)
    singleOf(::SettingsRepository)
    singleOf(::CountriesRepository)
    single { getCountryDao(get()) }
    
    viewModelOf(::SettingsViewModel)
    viewModelOf(::LocationDetailsChangeViewModel)
    viewModelOf(::AccountDetailsChangeViewModel)
    viewModelOf(::EmailChangeViewModel)
    viewModelOf(::EmailVerifyViewModel)
    viewModelOf(::PasswordChangeViewModel)
    viewModelOf(::PasswordVerifyViewModel)
    viewModelOf(::PasswordEmailEntryViewModel)
}
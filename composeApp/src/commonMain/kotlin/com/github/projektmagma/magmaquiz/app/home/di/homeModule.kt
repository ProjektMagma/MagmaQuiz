package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.home.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.home.data.service.SettingsService
import com.github.projektmagma.magmaquiz.app.home.data.service.UsersService
import com.github.projektmagma.magmaquiz.app.home.presentation.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    // Quiz
    singleOf(::QuizService)
    singleOf(::QuizRepository)
    viewModelOf(::QuizzesListViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::CreateQuizViewModel)

    // Users
    singleOf(::UsersService)
    singleOf(::UsersRepository)
    viewModelOf(::UsersViewModel)

    // Settings
    singleOf(::SettingsService)
    singleOf(::SettingsRepository)
    viewModelOf(::SettingsViewModel)
}
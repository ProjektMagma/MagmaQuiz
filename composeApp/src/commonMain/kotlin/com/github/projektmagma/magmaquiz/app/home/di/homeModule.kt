package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.home.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.home.data.service.SettingsService
import com.github.projektmagma.magmaquiz.app.home.data.service.UsersService
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.GameQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizDetailsViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.SettingsViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.UserDetailsViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    // Quiz
    singleOf(::QuizService)
    singleOf(::QuizRepository)
    viewModelOf(::QuizzesListViewModel)
    viewModelOf(::GameQuizViewModel)
    viewModelOf(::CreateQuizViewModel)
    viewModel { parameters ->
        QuizDetailsViewModel(id = parameters.get(), quizRepository = get())
    }

    // Users
    singleOf(::UsersService)
    singleOf(::UsersRepository)
    viewModelOf(::UsersViewModel)
    viewModelOf(::UserDetailsViewModel)

    // Settings
    singleOf(::SettingsService)
    singleOf(::SettingsRepository)
    viewModelOf(::SettingsViewModel)
}
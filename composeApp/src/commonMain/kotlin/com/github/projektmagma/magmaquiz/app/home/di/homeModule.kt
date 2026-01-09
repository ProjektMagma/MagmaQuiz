package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.home.data.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.data.QuizService
import com.github.projektmagma.magmaquiz.app.home.data.UsersRepository
import com.github.projektmagma.magmaquiz.app.home.data.UsersService
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.FavoritesQuizzesViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.UsersViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    // Quiz
    singleOf(::QuizService)
    singleOf(::QuizRepository)
    viewModelOf(::QuizzesListViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::FavoritesQuizzesViewModel)
    viewModelOf(::CreateQuizViewModel)

    // Users
    singleOf(::UsersService)
    singleOf(::UsersRepository)
    viewModelOf(::UsersViewModel)
}
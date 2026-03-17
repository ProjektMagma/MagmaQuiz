package com.github.projektmagma.magmaquiz.app.quizzes.di

import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizDetailsViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizReviewsViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizzesListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val quizModule = module {
    singleOf(::QuizService)
    singleOf(::QuizRepository)
    viewModelOf(::QuizzesListViewModel)
    viewModelOf(::CreateQuizViewModel)
    viewModel { parameters ->
        QuizReviewsViewModel(id = parameters.get(), quizRepository = get(), authRepository = get())
    }
    viewModel { parameters ->
        QuizDetailsViewModel(id = parameters.get(), quizRepository = get())
    }
}
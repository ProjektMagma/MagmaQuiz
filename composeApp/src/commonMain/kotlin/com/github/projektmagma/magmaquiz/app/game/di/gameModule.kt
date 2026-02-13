package com.github.projektmagma.magmaquiz.app.game.di

import com.github.projektmagma.magmaquiz.app.game.presentation.GameQuizViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gameModule = module {

    viewModelOf(::GameQuizViewModel)

}
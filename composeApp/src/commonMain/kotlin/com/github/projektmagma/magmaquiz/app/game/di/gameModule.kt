package com.github.projektmagma.magmaquiz.app.game.di

import com.github.projektmagma.magmaquiz.app.game.data.repository.GameRepository
import com.github.projektmagma.magmaquiz.app.game.data.service.GameService
import com.github.projektmagma.magmaquiz.app.game.presentation.GameLeaderboardViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.GameMultiplayerViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.GameQuizViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.GameSettingsViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.GameWaitViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gameModule = module {
    singleOf(::GameService)
    singleOf(::GameRepository)

    viewModelOf(::GameQuizViewModel)
    viewModelOf(::GameSettingsViewModel)
    viewModelOf(::GameWaitViewModel)
    viewModelOf(::GameMultiplayerViewModel)
    viewModelOf(::GameLeaderboardViewModel)
}
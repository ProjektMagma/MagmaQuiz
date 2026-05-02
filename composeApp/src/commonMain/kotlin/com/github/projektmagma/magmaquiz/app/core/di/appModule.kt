package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.auth.di.authModule
import com.github.projektmagma.magmaquiz.app.game.di.gameModule
import com.github.projektmagma.magmaquiz.app.home.di.homeModule
import com.github.projektmagma.magmaquiz.app.quizzes.di.quizModule
import com.github.projektmagma.magmaquiz.app.settings.di.settingsModule
import com.github.projektmagma.magmaquiz.app.users.di.usersModule

val appModule = listOf(
    sharedModule,
    platformModule,
    authModule,
    gameModule,
    homeModule,
    quizModule,
    settingsModule,
    usersModule
)
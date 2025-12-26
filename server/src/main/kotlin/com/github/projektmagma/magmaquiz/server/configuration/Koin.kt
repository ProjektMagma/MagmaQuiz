package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.controllers.AuthDataController
import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.controllers.SettingsDataController
import com.github.projektmagma.magmaquiz.server.controllers.UsersDataController
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(serverMainModule)
    }
}

val serverMainModule = module {
    single { AuthDataController() }
    single { SettingsDataController() }
    single { QuizDataController() }
    single { UsersDataController() }
}
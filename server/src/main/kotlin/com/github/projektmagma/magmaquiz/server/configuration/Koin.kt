package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.controllers.*
import com.github.projektmagma.magmaquiz.server.repository.FriendshipRepository
import com.github.projektmagma.magmaquiz.server.repository.QuizRepository
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.server.storage.ExposedSessionStorage
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
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
    singleOf(::ExposedSessionStorage)

    singleOf(::UserRepository)
    singleOf(::FriendshipRepository)
    singleOf(::QuizRepository)

    singleOf(::AuthDataController)
    singleOf(::SettingsDataController)
    singleOf(::QuizDataController)
    singleOf(::UsersDataController)
    singleOf(::RoomsDataController)
}
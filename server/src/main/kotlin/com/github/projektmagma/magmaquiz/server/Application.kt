package com.github.projektmagma.magmaquiz.server

import com.github.projektmagma.magmaquiz.server.configuration.*
import com.github.projektmagma.magmaquiz.server.routes.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.mainModule() {
    configureKoin()
    configureDatabase()
    configureMailer()
    configureAuth()
    configureCORS()
    configureSessions(get())
    configureContentNegotiation()
    configureWebSockets()

    authRoutes(get(), get())
    settingsRoutes(get(), get())
    quizRoutes(get())
    usersRoutes(get())
    tagRoutes(get())
    reviewRoutes(get())
    multiplayerRoutes(get())
}
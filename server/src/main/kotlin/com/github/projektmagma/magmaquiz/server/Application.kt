package com.github.projektmagma.magmaquiz.server

import com.github.projektmagma.magmaquiz.server.configuration.*
import com.github.projektmagma.magmaquiz.server.routes.authRoutes
import com.github.projektmagma.magmaquiz.server.routes.quizRoutes
import com.github.projektmagma.magmaquiz.server.routes.settingsRoutes
import com.github.projektmagma.magmaquiz.server.routes.usersRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.mainModule() {
    configureKoin()
    configureDatabase()
    configureAuth()
    configureCORS()
    configureSessions(get())
    configureContentNegotiation()

    authRoutes(get(), get())
    settingsRoutes(get())
    quizRoutes(get())
    usersRoutes(get())
}
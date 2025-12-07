package com.github.projektmagma.magmaquiz.server

import com.github.projektmagma.magmaquiz.server.configuration.configureAuth
import com.github.projektmagma.magmaquiz.server.configuration.configureCORS
import com.github.projektmagma.magmaquiz.server.configuration.configureContentNegotiation
import com.github.projektmagma.magmaquiz.server.configuration.configureDatabase
import com.github.projektmagma.magmaquiz.server.configuration.configureKoin
import com.github.projektmagma.magmaquiz.server.configuration.configureSessions
import com.github.projektmagma.magmaquiz.server.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.mainModule() {
    configureKoin()
    configureDatabase()
    configureAuth()
    configureCORS()
    configureSessions()
    configureContentNegotiation()

    authRoutes(get())
}
package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import java.io.File

fun Application.configureSessions() {
    install(Sessions) {
        header<UserSession>("user_session", directorySessionStorage(File("build/.sessions")))
    }
}
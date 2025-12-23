package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlin.time.Duration

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAge = Duration.INFINITE
        }
    }
}
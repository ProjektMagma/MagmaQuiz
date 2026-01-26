package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.storage.ExposedSessionStorage
import com.github.projektmagma.magmaquiz.shared.data.domain.CustomHeaders
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSessions(exposedSessionStorage: ExposedSessionStorage) {
    install(Sessions) {
        header<UserSession>(CustomHeaders.UserSession, exposedSessionStorage)
    }
}


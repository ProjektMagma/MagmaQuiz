package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.shared.data.domain.CustomHeaders
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.SetCookie)
        allowHeader(CustomHeaders.UserSession)
    }
}
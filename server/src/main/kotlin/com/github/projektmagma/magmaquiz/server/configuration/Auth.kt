package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respond

fun Application.configureAuth() {
    install(Authentication) {
        session<UserSession>("session-auth") {
            validate { session ->
                session
            }
            challenge {
                call.respond(message = "User not authorized.", status = HttpStatusCode.Unauthorized)
            }
        }
    }
}
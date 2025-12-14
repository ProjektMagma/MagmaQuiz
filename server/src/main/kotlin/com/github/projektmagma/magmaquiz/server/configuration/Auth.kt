package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth() {
    install(Authentication) {
        session<UserSession>("session-auth") {
            validate { session ->
                session
            }
            challenge {
                call.respondToResource(NetworkResource.Error(HttpStatusCode.Unauthorized))
            }
        }
    }
}
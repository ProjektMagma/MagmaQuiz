package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

fun Application.configureAuth() {
    install(Authentication) {
        session<UserSession>(AuthTypes.SessionAuth) {
            validate { session ->
                val dbUser =
                    transaction { UserEntity.findById(session.userId).apply { this?.lastActivity = Instant.now() } }

                if (dbUser == null) null
                else session
            }
            challenge {
                call.respondToResource(NetworkResource.Error(HttpStatusCode.Unauthorized))
            }
        }
    }
}
package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.AuthDataController
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.LoginUserValue
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.authRoutes(authDataController: AuthDataController) {
    routing {
        route("/auth") {
            post("/login") {
                val loginUserValue = call.receive<LoginUserValue>()

                val resource = authDataController.authLogin(loginUserValue)

                if (resource is NetworkResource.Success) {
                    call.sessions.set(UserSession(resource.data.userId!!, resource.data.userName))
                }

                call.respondToResource(resource)


            }


            post("/register") {
                val postContent = call.receive<CreateUserValue>()

                val resource = authDataController.authRegister(postContent)

                if (resource is NetworkResource.Success) {
                    call.sessions.set(UserSession(resource.data.userId!!, resource.data.userName))
                }

                call.respondToResource(resource)


            }

            authenticate("session-auth") {

                get("/whoami") {
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(authDataController.authWhoami(session))
                }

                get("/logout") {
                    call.sessions.clear<UserSession>()
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
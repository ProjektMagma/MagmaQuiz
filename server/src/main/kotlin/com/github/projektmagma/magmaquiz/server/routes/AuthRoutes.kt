package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.server.controllers.UserDataController
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.authRoutes(userDataController: UserDataController) {
    routing {
        route("/user") {
            post("/login") {
                if (call.sessions.get<UserSession>() != null) {
                    call.respondToResource(NetworkResource.Error(HttpStatusCode.Conflict))
                    return@post
                }
                val loginUserValue = call.receive<LoginUserValue>()

                val resource = userDataController.tryLoginUser(loginUserValue)

                if (resource is NetworkResource.Success) {
                    call.sessions.set(UserSession(resource.data.userId!!, resource.data.userName))
                }

                call.respondToResource(resource)


            }


            post("/register") {
                val postContent = call.receive<CreateUserValue>()

                val resource = userDataController.tryRegisterUser(postContent)

                if (resource is NetworkResource.Success) {
                    call.sessions.set(UserSession(resource.data.userId!!, resource.data.userName))
                }

                call.respondToResource(resource)


            }

            authenticate("session-auth") {

                get("/whoami") {
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(userDataController.tryFindUser(session))
                }

                post("/changePassword") {
                    val session = call.sessions.get<UserSession>()!!
                    val postContent = call.receive<ChangePasswordValue>()


                    call.respondToResource(
                        userDataController.changePassword(
                            session, postContent
                        )
                    )
                }

                get("/logout") {
                    call.sessions.clear<UserSession>()
                    call.respond(HttpStatusCode.OK)
                }

                get("/delete") {
                    val session = call.sessions.get<UserSession>()!!
                    call.sessions.clear<UserSession>()
                    call.respondToResource(userDataController.changeActiveStatus(session, false))
                }
            }
        }
    }
}
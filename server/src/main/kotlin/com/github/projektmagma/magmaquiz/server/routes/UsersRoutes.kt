package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.UsersDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

fun Application.usersRoutes(usersDataController: UsersDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("/users") {
                route("/friendship") {
                    get("/sendInvitation/{userId}") {
                        val session = call.sessions.get<UserSession>()!!
                        val userId = UUID.fromString(call.parameters["id"]!!)

                        call.respondToResource(
                            usersDataController.usersFriendshipSendInvitation(session, userId)
                        )
                    }

                    get("/acceptInvitation/{userId}") {
                        val session = call.sessions.get<UserSession>()!!
                        val userId = UUID.fromString(call.parameters["id"]!!)

                        call.respondToResource(
                            usersDataController.usersFriendshipAcceptInvitation(session, userId)
                        )
                    }

                    get("/friendList") {
                        val session = call.sessions.get<UserSession>()!!

                        call.respondToResource(
                            usersDataController.usersFriendshipFriendList(session)
                        )
                    }
                }

                get("/find/") {
                    call.respondToResource(
                        usersDataController.usersFindByUserName()
                    )
                }

                get("/find/{userName}") {
                    val userName = call.parameters["userName"]!!

                    call.respondToResource(
                        usersDataController.usersFindByUserName(userName)
                    )
                }

                get("/userData/{id}") {
                    val userId = UUID.fromString(call.parameters["id"]!!)

                    call.respondToResource(
                        usersDataController.usersUserData(userId)
                    )
                }
            }
        }
    }
}
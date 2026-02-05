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
                        val userId = UUID.fromString(call.parameters["userId"]!!)

                        call.respondToResource(
                            usersDataController.usersFriendshipSendInvitation(session, userId)
                        )
                    }

                    get("/acceptInvitation/{userId}") {
                        val session = call.sessions.get<UserSession>()!!
                        val userId = UUID.fromString(call.parameters["userId"]!!)

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

                    get("/incoming") {
                        val session = call.sessions.get<UserSession>()!!

                        call.respondToResource(
                            usersDataController.usersFriendshipIncoming(session)
                        )
                    }

                    get("/outgoing") {
                        val session = call.sessions.get<UserSession>()!!

                        call.respondToResource(
                            usersDataController.usersFriendshipOutgoing(session)
                        )
                    }
                }

                get("/find/") {
                    val session = call.sessions.get<UserSession>()!!
                    call.respondToResource(
                        usersDataController.usersFindByUserName(session)
                    )
                }

                get("/find/{userName}") {
                    val session = call.sessions.get<UserSession>()!!
                    val userName = call.parameters["userName"]!!

                    call.respondToResource(
                        usersDataController.usersFindByUserName(session, userName)
                    )
                }

                get("/userData/{userId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val userId = UUID.fromString(call.parameters["userId"]!!)

                    call.respondToResource(
                        usersDataController.usersUserData(session, userId)
                    )
                }
            }
        }
    }
}
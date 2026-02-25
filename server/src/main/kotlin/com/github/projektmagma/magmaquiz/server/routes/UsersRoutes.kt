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

                    get("/friendList/{count}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipFriendList(session, count, null)
                        )
                    }
                    
                    get("/friendList/{count}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]
                        val count = call.parameters["count"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipFriendList(session, count, stringToSearch)
                        )
                    }

                    get("/incoming/{count}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipIncoming(session, count, null)
                        )
                    }
                    
                    get("/incoming/{count}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]
                        val count = call.parameters["count"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipIncoming(session, count, stringToSearch)
                        )
                    }

                    get("/outgoing/{count}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipOutgoing(session, count, null)
                        )
                    }
                    
                    get("/outgoing/{count}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]
                        val count = call.parameters["count"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipOutgoing(session, count, stringToSearch)
                        )
                    }
                }

                get("/find/{count}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(
                        usersDataController.usersFindByUserName(session, count, null)
                    )
                }

                get("/find/{count}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val stringToSearch = call.parameters["stringToSearch"]

                    call.respondToResource(
                        usersDataController.usersFindByUserName(session, count, stringToSearch)
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
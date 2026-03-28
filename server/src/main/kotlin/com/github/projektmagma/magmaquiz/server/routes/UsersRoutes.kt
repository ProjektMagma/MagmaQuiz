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

                    get("/friendList/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipFriendList(session, count, offset, "")
                        )
                    }

                    get("/friendList/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipFriendList(session, count, offset, stringToSearch)
                        )
                    }

                    get("/incoming/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipIncoming(session, count, offset, "")
                        )
                    }

                    get("/incoming/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipIncoming(session, count, offset, stringToSearch)
                        )
                    }

                    get("/outgoing/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipOutgoing(session, count, offset, "")
                        )
                    }

                    get("/outgoing/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            usersDataController.usersFriendshipOutgoing(session, count, offset, stringToSearch)
                        )
                    }
                }

                get("/find/{count}/{offset}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()

                    call.respondToResource(
                        usersDataController.usersFindByUserName(session, count, offset, "")
                    )
                }

                get("/find/{count}/{offset}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()
                    val stringToSearch = call.parameters["stringToSearch"]!!

                    call.respondToResource(
                        usersDataController.usersFindByUserName(session, count, offset, stringToSearch)
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
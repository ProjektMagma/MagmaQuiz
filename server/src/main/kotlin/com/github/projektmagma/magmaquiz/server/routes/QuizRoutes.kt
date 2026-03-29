package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import java.util.UUID

fun Application.quizRoutes(quizDataController: QuizDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("/quiz") {
                get("/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizFromId(quizId, session))

                }

                get("/find/{count}/{offset}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()

                    call.respondToResource(quizDataController.quizFindByName(session, count, offset, ""))
                }

                get("/find/{count}/{offset}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()

                    call.respondToResource(quizDataController.quizFindByName(session, count, offset, stringToSearch))
                }

                post("/create") {
                    val postContent = call.receive<CreateOrModifyQuizValue>()
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(
                        quizDataController.quizCreate(
                            postContent,
                            session
                        )
                    )
                }

                post("/modify") {
                    val postContent = call.receive<CreateOrModifyQuizValue>()
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(
                        quizDataController.quizModify(
                            postContent,
                            session
                        )
                    )
                }

                get("/changeFavoriteStatus/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)

                    call.respondToResource(quizDataController.quizChangeFavoriteStatus(quizId, session))
                }

                get("/MyGameHistory/{count}/{offset}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()

                    call.respondToResource(quizDataController.quizMyGameHistory(session, count, offset))
                }

                get("/markAsPlayed/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizMarkAsPlayed(session, quizId))
                }

                get("/findByUser/{count}/{offset}/{userId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val offset = call.parameters["offset"]!!.toInt()
                    val userId = UUID.fromString(call.parameters["userId"]!!)

                    call.respondToResource(quizDataController.quizFindByUserId(session, count, offset, userId))
                }

                delete("/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizDelete(session, quizId))
                }

                route("/myFavorites")
                {
                    get("/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(quizDataController.quizMyFavorites(session, count, offset, ""))
                    }

                    get("/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            quizDataController.quizMyFavorites(
                                session,
                                count,
                                offset,
                                stringToSearch
                            )
                        )
                    }
                }

                route("/newest") {
                    get("/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(quizDataController.quizNewest(session, count, offset, ""))
                    }


                    get("/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(quizDataController.quizNewest(session, count, offset, stringToSearch))
                    }
                }

                route("/mostLiked")
                {
                    get("/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(quizDataController.quizMostLiked(session, count, offset, ""))
                    }

                    get("/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(quizDataController.quizMostLiked(session, count, offset, stringToSearch))
                    }
                }

                route("/friendsQuizzes") {
                    get("/{count}/{offset}/") {
                        val session = call.sessions.get<UserSession>()!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(quizDataController.quizFriendsQuizzes(session, count, offset, ""))
                    }

                    get("/{count}/{offset}/{stringToSearch}") {
                        val session = call.sessions.get<UserSession>()!!
                        val stringToSearch = call.parameters["stringToSearch"]!!
                        val count = call.parameters["count"]!!.toInt()
                        val offset = call.parameters["offset"]!!.toInt()

                        call.respondToResource(
                            quizDataController.quizFriendsQuizzes(
                                session,
                                count,
                                offset,
                                stringToSearch
                            )
                        )
                    }
                }
            }
        }
    }
}

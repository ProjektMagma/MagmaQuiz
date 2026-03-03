package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

fun Application.quizRoutes(quizDataController: QuizDataController) {
    routing {
        authenticate(AuthTypes.SessionAuth) {
            route("/quiz") {
                get("/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizFromId(quizId, session))

                }

                get("/find/{count}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizFindByName(session, count, null))
                }

                get("/find/{count}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizFindByName(session, count, stringToSearch))
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

                get("/MyGameHistory/{count}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizMyGameHistory(session, count))
                }

                get("/findByUser/{count}/{userId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    val userId = UUID.fromString(call.parameters["userId"]!!)

                    call.respondToResource(quizDataController.quizFindByUserId(session, count, userId))
                }

                delete("/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizDelete(session, quizId))
                }

                get("/myFavorites/{count}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizMyFavorites(session, count, null))
                }

                get("/myFavorites/{count}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizMyFavorites(session, count, stringToSearch))
                }

                get("/newest/{count}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizNewest(session, count, null))
                }


                get("/newest/{count}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizNewest(session, count, stringToSearch))
                }

                get("/mostLiked/{count}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizMostLiked(session, count, null))
                }

                get("/mostLiked/{count}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizMostLiked(session, count, stringToSearch))
                }

                get("/friendsQuizzes/{count}/") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizFriendsQuizzes(session, count, null))
                }

                get("/friendsQuizzes/{count}/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizFriendsQuizzes(session, count, stringToSearch))
                }

                get("/markAsPlayed/{quizId}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["quizId"]!!)
                    call.respondToResource(quizDataController.quizMarkAsPlayed(session, quizId))
                }

                route("/reviews") {
                    get("/{quizId}") {
                        val quizId = UUID.fromString(call.parameters["quizId"]!!)
                        call.respondToResource(quizDataController.quizReviews(quizId))

                    }

                    post("/create/{quizId}") {
                        val session = call.sessions.get<UserSession>()!!
                        val quizId = UUID.fromString(call.parameters["quizId"]!!)
                        val review = call.receive<QuizReview>()

                        call.respondToResource(quizDataController.quizCreateReview(session, quizId, review))
                    }

                    delete("/delete/{quizId}") {
                        val session = call.sessions.get<UserSession>()!!
                        val quizId = UUID.fromString(call.parameters["quizId"]!!)

                        call.respondToResource(quizDataController.quizDeleteReview(session, quizId))
                    }
                }

                get("/tags/{count}/") {
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizGetPossibleTags(count, null))
                }

                get("/tags/{count}/{stringToSearch}") {
                    val stringToSearch = call.parameters["stringToSearch"]
                    val count = call.parameters["count"]!!.toInt()

                    call.respondToResource(quizDataController.quizGetPossibleTags(count, stringToSearch))
                }
            }
        }
    }
}

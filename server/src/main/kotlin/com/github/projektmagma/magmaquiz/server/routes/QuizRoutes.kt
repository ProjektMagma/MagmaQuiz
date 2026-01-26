package com.github.projektmagma.magmaquiz.server.routes

import com.github.projektmagma.magmaquiz.server.controllers.QuizDataController
import com.github.projektmagma.magmaquiz.server.data.util.AuthTypes
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.data.util.respondToResource
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
                get("/{id}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["id"]!!)
                    call.respondToResource(quizDataController.quizFromId(quizId, session))

                }

                get("/find/") {
                    val session = call.sessions.get<UserSession>()!!

                    call.respondToResource(quizDataController.quizFindByName(session))
                }

                get("/find/{stringToSearch}") {
                    val session = call.sessions.get<UserSession>()!!
                    val stringToSearch = call.parameters["stringToSearch"]

                    call.respondToResource(quizDataController.quizFindByName(session, stringToSearch))
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

                get("/changeFavoriteStatus/{id}") {
                    val session = call.sessions.get<UserSession>()!!
                    val quizId = UUID.fromString(call.parameters["id"]!!)
                    call.respondToResource(quizDataController.quizChangeFavoriteStatus(quizId, session))
                }

                get("/myFavorites") {
                    val session = call.sessions.get<UserSession>()!!
                    call.respondToResource(quizDataController.quizMyFavorites(session))
                }

                get("/findByUser/{id}") {
                    val session = call.sessions.get<UserSession>()!!
                    val userId = UUID.fromString(call.parameters["id"]!!)
                    call.respondToResource(quizDataController.quizFindByUserId(userId, session))
                }

                delete("/{id}") {
                    val quizId = UUID.fromString(call.parameters["id"]!!)
                    call.respondToResource(quizDataController.quizDelete(quizId))
                }

                get("/newest/{count}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizNewest(session, count))
                }

                get("/mostLiked/{count}") {
                    val session = call.sessions.get<UserSession>()!!
                    val count = call.parameters["count"]!!.toInt()
                    call.respondToResource(quizDataController.quizMostLiked(session, count))
                }

                get("/friendsQuizzes/") {
                    val session = call.sessions.get<UserSession>()!!
                    call.respondToResource(quizDataController.quizFriendsQuizzes(session))
                }
            }
        }
    }
}
